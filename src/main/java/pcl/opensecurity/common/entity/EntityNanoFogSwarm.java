package pcl.opensecurity.common.entity;
/**
 * @author ben_mkiv
 */
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityNanoFog;
import pcl.opensecurity.common.tileentity.TileEntityNanoFogTerminal;
import pcl.opensecurity.util.ItemUtils;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import static pcl.opensecurity.client.models.ModelNanoFogSwarm.drawCube;
import static pcl.opensecurity.client.models.ModelNanoFogSwarm.resolution;

public class EntityNanoFogSwarm extends Entity {
    public static final String NAME = "opensecurity.nanofogswarm";

    private Vec3d target = new Vec3d(0, 0, 0);

    public static final int maxProgress = 90, buildNotifyProgress = 30;
    static final double maxSpeed = 1;
    double speed = maxSpeed;

    public boolean targetReached = false;

    private boolean clientDataComplete = false;

    private static final DataParameter<BlockPos> FOG = EntityDataManager.<BlockPos>createKey(EntityNanoFogSwarm.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<BlockPos> TERMINAL = EntityDataManager.<BlockPos>createKey(EntityNanoFogSwarm.class, DataSerializers.BLOCK_POS);
    private static final DataParameter<Boolean> RETURNTASK = EntityDataManager.<Boolean>createKey(EntityNanoFogSwarm.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BUILDTASK = EntityDataManager.<Boolean>createKey(EntityNanoFogSwarm.class, DataSerializers.BOOLEAN);

    double speedX, speedY, speedZ;

    int maxTickAge = Integer.MIN_VALUE;

    ItemStackHandler inventory = new ItemStackHandler(1);

    public int buildProgress = 0;

    public long blockJobDone = 0;

    public ArrayList<Cube> cubes = new ArrayList<>();

    public class Cube {
        float x, y, z;
        Float[] r1, r2;

        void setupRandom(Random rand){
            r1 = new Float[]{ rand.nextFloat(), rand.nextFloat(), rand.nextFloat() };
            r2 = new Float[]{ rand.nextFloat(), rand.nextFloat(), rand.nextFloat() };
        }

        Cube(float x1, float y1, float z1){
            x=x1; y=y1; z=z1;
            setupRandom(rand);
        }

        float interpolate(float current, float max){
            return 1f/max * Math.min(current, max);
        }

        @SideOnly(Side.CLIENT)
        public void render(float partialTicks, float scale, float randOffsetFactor){
            float offsetFactor = 20 * interpolate(ticksExisted % 60, 60);

            float renderX = x + randOffsetFactor * (r1[0] + (r2[0] * offsetFactor));
            float renderY = y + randOffsetFactor * (r1[1] + (r2[1] * offsetFactor));
            float renderZ = z + randOffsetFactor * (r1[2] + (r2[2] * offsetFactor));

            GlStateManager.pushMatrix();
            GlStateManager.translate(renderX, renderY, renderZ);
            drawCube(scale);
            GlStateManager.popMatrix();
        }
    }

    @SideOnly(Side.CLIENT)
    void initModelData(){
        for(int x=0; x < resolution; x++)
            for(int y=0; y < resolution; y++)
                for(int z=0; z < resolution; z++){
                    //dont add cubes which are within the cube
                    if(y > 0 && y < (resolution-1))
                        if(x!=0 && x != (resolution-1))
                            if(z!=0 && z != (resolution-1))
                                continue;

                    float resHalf = (float) resolution / 2f;
                    Cube c = new Cube(x - resHalf, y - resHalf, z - resHalf);
                    cubes.add(c);
                }
    }

    public EntityNanoFogSwarm(World world) {
        super(world);
        this.isImmuneToFire = true;
        this.noClip = true;
        setSize(1F, 1F);
        setNoGravity(true);
        setEntityInvulnerable(true);

        if(world.isRemote)
            initModelData();
    }

    @Override
    public void entityInit(){
        this.dataManager.register(RETURNTASK, false);
        this.dataManager.register(BUILDTASK, false);
        this.dataManager.register(FOG, new BlockPos(0, 0, 0));
        this.dataManager.register(TERMINAL, new BlockPos(0, 0, 0));
    }

    BlockPos getFogBlock(){
        return this.dataManager.get(FOG);
    }

    BlockPos getTerminalBlock(){
        return this.dataManager.get(TERMINAL);
    }

    public boolean isReturnTask(){ return this.dataManager.get(RETURNTASK); }

    public boolean isBuildTask(){ return this.dataManager.get(BUILDTASK); }

    public boolean canWork(){
        return !world.isRemote || clientDataComplete;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key){
        if(!world.isRemote || clientDataComplete)
            return;

        updateClient();
    }


    public void updateClient(){
        if(getFogBlock().equals(new BlockPos(0, 0, 0)))
            return;

        if(getTerminalBlock().equals(new BlockPos(0, 0, 0)))
            return;

        if(isReturnTask()){
            target = new Vec3d(getTerminalBlock()).add(new Vec3d(0.5, 0, 0.5));
            buildProgress = buildNotifyProgress + 10;
            clientDataComplete = true;
        } else if(isBuildTask()){
            target = new Vec3d(getFogBlock()).add(new Vec3d(0.5, 0, 0.5));
            buildProgress = 0;
            clientDataComplete = true;
        }
    }

    private void calculateMaxAge(){
        maxTickAge = maxProgress + 20 * (int) Math.ceil(new Vec3d(getPosition()).distanceTo(target) / (maxSpeed/2));
    }

    public void setTravelToFogBlock(BlockPos terminal, BlockPos pos){
        this.dataManager.set(BUILDTASK, true);
        this.dataManager.set(TERMINAL, terminal);
        this.dataManager.set(FOG, pos);
        setPosition(terminal.getX() + 0.5, terminal.getY(), terminal.getZ() + 0.5);

        target = new Vec3d(getFogBlock()).add(new Vec3d(0.5, 0, 0.5));
        buildProgress = 0;
        calculateMaxAge();
    }

    public void setTravelToTerminal(BlockPos pos, BlockPos terminal){
        this.dataManager.set(RETURNTASK, true);
        this.dataManager.set(TERMINAL, terminal);
        this.dataManager.set(FOG, pos);
        setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        target = new Vec3d(getTerminalBlock()).add(new Vec3d(0.5, 0, 0.5));
        buildProgress = buildNotifyProgress + 10;

        calculateMaxAge();
    }

    static float interpolate(float current, float max, float factor){
        return factor * (1f/max * Math.min(current, max));
    }

    boolean setMotion(){
        double distance = new Vec3d(getPosition()).distanceTo(target);

        double d0 = (target.x - this.posX);
        double d1 = (target.y - this.posY);
        double d2 = (target.z - this.posZ);

        //return if we reached the target
        if(Math.abs(d0) < 0.01 && Math.abs(d1) < 0.01 && Math.abs(d2) < 0.01) {
            setPosition(target.x, target.y, target.z);
            targetReached = true;
            return false;
        }

        //slow down movement when getting closer to the target block
        if(Math.abs(distance) < 5) {
            speed = maxSpeed - Math.abs(interpolate(5f-(float) Math.abs(distance), 5, 0.7f));
        }

        //slow down movement when leaving terminal
        if(ticksExisted < 20) {
            speed = maxSpeed - interpolate(20-ticksExisted, 20, 0.7f);
        }

        speedX = speed/distance * d0;
        speedY = speed/distance * d1;
        speedZ = speed/distance * d2;

        return true;
    }

    @Override
    public void onEntityUpdate(){
        super.onEntityUpdate();

        checkEntityAge();
    }

    @Override
    public void onUpdate(){
        super.onUpdate();

        if(!canWork()){
            return;
        }

        if(isReturnTask())
            updateReturning();
        else if(isBuildTask())
            updateBuild();
    }


    void updateReturning(){
        buildProgress--;

        if(blockJobDone == 0 && ticksExisted >= 5) {
            TileEntity te = world.getTileEntity(getFogBlock());

            if(te != null && te instanceof TileEntityNanoFog) {
                inventory.setStackInSlot(0, ((TileEntityNanoFog) te).notifyRemove());
                blockJobDone = ticksExisted;
            }
        }

        if(!targetReached && ticksExisted > buildNotifyProgress)
            moveTowardsTarget();

        if(!world.isRemote && targetReached) {
            TileEntityNanoFogTerminal te = getTerminal();
            if(te != null)
                te.returnFogMaterial(inventory.getStackInSlot(0));
            else
                ItemUtils.dropItem(inventory.getStackInSlot(0), getEntityWorld(), getPosition(), false, 10);
            setDead();
        }
    }

    TileEntityNanoFogTerminal getTerminal(){
        TileEntity te = world.getTileEntity(getTerminalBlock());
        return te instanceof TileEntityNanoFogTerminal ? (TileEntityNanoFogTerminal) te : null;
    }

    void updateBuild(){
        if(!targetReached)
            moveTowardsTarget();
        else
            updateConstructionProgress();
    }

    void moveTowardsTarget(){
        if(target.equals(new Vec3d(0, 0, 0)))
            return;

        if (!setMotion())
            return;

        if (world.isRemote)
            setVelocity(speedX, speedY, speedZ);

        setPosition(posX + speedX, posY + speedY, posZ + speedZ);
    }


    void checkEntityAge(){
        if(world.isRemote || ticksExisted < maxTickAge)
            return;

        Logger.getLogger(OpenSecurity.MODID).info("killing NanoFog swarm which lived too long... age: " + ticksExisted + "/" + maxTickAge);
        this.setDead();
    }


    void updateConstructionProgress(){
        // client + server update
        buildProgress++;

        if(blockJobDone == 0 && buildProgress >= buildNotifyProgress) {
            TileEntity te = world.getTileEntity(getFogBlock());

            if(te != null && te instanceof TileEntityNanoFog) {
                ((TileEntityNanoFog) te).notifyBuild();
                blockJobDone = ticksExisted;
            }
        }

        if(!world.isRemote && buildProgress > maxProgress)
            this.setDead();

    }


    @Override
    public void writeEntityToNBT(NBTTagCompound tag){
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag){

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){

        this.dataManager.set(RETURNTASK, nbt.getBoolean("return"));
        this.dataManager.set(BUILDTASK, nbt.getBoolean("build"));

        BlockPos fog = new BlockPos(nbt.getInteger("fogX"), nbt.getInteger("fogY"), nbt.getInteger("fogZ"));
        BlockPos terminal = new BlockPos(nbt.getInteger("terminalX"), nbt.getInteger("terminalY"), nbt.getInteger("terminalZ"));

        inventory.deserializeNBT(nbt.getCompoundTag("inv"));

        if(isReturnTask())
            setTravelToTerminal(fog, terminal);
        else
            setTravelToFogBlock(terminal, fog);

        super.readFromNBT(nbt);
    }

    @Override
    public boolean shouldRenderInPass(int pass){
        return pass == 1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("fogX", getFogBlock().getX());
        nbt.setInteger("fogY", getFogBlock().getY());
        nbt.setInteger("fogZ", getFogBlock().getZ());

        nbt.setInteger("terminalX", getTerminalBlock().getX());
        nbt.setInteger("terminalY", getTerminalBlock().getY());
        nbt.setInteger("terminalZ", getTerminalBlock().getZ());

        nbt.setBoolean("return", isReturnTask());
        nbt.setBoolean("build", isBuildTask());

        nbt.setTag("inv", inventory.serializeNBT());

        return super.writeToNBT(nbt);
    }


    public Random getRNG(){
        return rand;
    }
}

