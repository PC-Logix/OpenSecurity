package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.lib.easing.penner.Quad;
import pcl.opensecurity.util.RolldoorHelper;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;

public class TileEntityRolldoorController extends TileEntityDoorController {
    public TileEntityRolldoorController(){
        super("os_rolldoorcontroller");
    }

    private EnumFacing facing = EnumFacing.NORTH;

    private double currentPosition = 0;

    private long animationStart = 0;
    private double speed = 0;
    private double moveSpeed = 0.5;

    private HashSet<WeakReference<TileEntityRolldoor>> elements = new HashSet<>();
    private HashSet<BlockPos> elementsPos = new HashSet<>();

    @Override
    public void update(){
        super.update();

        if(elements.size() != elementsPos.size() && getWorld() != null){
            elements.clear();
            for(BlockPos pos : elementsPos){
                TileEntity tile = getWorld().getTileEntity(pos);
                if(tile instanceof TileEntityRolldoor)
                    elements.add(new WeakReference<>((TileEntityRolldoor) tile));
            }
        }

        if(speed() != 0)
            getCurrentHeight();
    }

    // OC Callbacks
    @Callback
    @Override
    public Object[] isOpen(Context context, Arguments args) {
        return new Object[] { getCurrentHeight() == 0 };
    }

    @Callback
    @Override
    public Object[] toggle(Context context, Arguments args) {
        if(speed != 0)
            setSpeed(speed * -1);
        else if(currentPosition == 0)
            setSpeed(moveSpeed);
        else
            setSpeed(-moveSpeed);

        return new Object[]{ true };
    }

    @Callback
    @Override
    public Object[] open(Context context, Arguments args) {
        setSpeed(-moveSpeed);
        return new Object[]{ true };
    }

    @Callback
    @Override
    public Object[] close(Context context, Arguments args) {
        setSpeed(moveSpeed);
        return new Object[]{ true };
    }

    @Callback
    @Override
    public Object[] removePassword(Context context, Arguments args) {
        return new Object[] { false, "not supported yet"};
    }

    @Callback
    @Override
    public Object[] setPassword(Context context, Arguments args) {
        return new Object[] { false, "not supported yet"};
    }

    @Callback
    public Object[] setSpeed(Context context, Arguments args) {
        moveSpeed = Math.max(0.1, Math.min(1, args.optDouble(0, moveSpeed))); // 0.1d - 1d
        return new Object[] { moveSpeed };
    }

    @Callback
    public Object[] getHeight(Context context, Arguments args) {
        ArrayList<Integer> heights = new ArrayList<>();

        for(WeakReference<TileEntityRolldoor> ref : elements){
            if(ref.get() != null && !ref.get().isInvalid())
                heights.add(ref.get().height());
        }

        return new Object[]{ heights.toArray() };
    }


    public void initialize(){
        elements.clear();
        elementsPos.clear();

        for(TileEntityRolldoor tile : RolldoorHelper.getDoors(this).values())
            addElement(tile);
    }

    public void remove(){
        for(WeakReference<TileEntityRolldoor> ref : new HashSet<WeakReference<TileEntityRolldoor>>(Arrays.asList(elements.toArray())))
            if(ref.get() != null && !ref.get().isInvalid())
                removeElement(ref.get());
    }

    private TileEntityRolldoor getRolldoor(BlockPos pos){
        TileEntity tile = getWorld().getTileEntity(pos);
        return tile instanceof TileEntityRolldoor ? (TileEntityRolldoor) tile : null;
    }

    private void addElement(TileEntityRolldoor rolldoor){
        if(rolldoor != null && !rolldoor.isInvalid()) {
            rolldoor.setOrigin(getPos());
            facing = rolldoor.getFacing();
            elements.add(new WeakReference<>(rolldoor));
            elementsPos.add(rolldoor.getPos());
        }
    }

    private void removeElement(TileEntityRolldoor rolldoor){
        if(rolldoor != null)
            rolldoor.setOrigin(null);

        initialize();
    }

    private void setSpeed(double speedIn){
        speed = speedIn;
        markDirtyClient();
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox(){
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        for(WeakReference<TileEntityRolldoor> ref : elements){
            if(ref.get() != null && !ref.get().isInvalid())
                bb = bb.union(ref.get().getElementsBoundingBox());
        }
        return bb;
    }

    public double getCurrentHeight(){
        if(animationStart == 0) {
            if(speed() != 0)
                animationStart = System.currentTimeMillis();
            else
                return currentPosition;
        }

        double height = getHeight();

        long prog = System.currentTimeMillis() - animationStart;

        double duration = Math.abs(height * speed()) * 1000;

        if(prog >= duration) {
            animationStart = 0;
            setSpeed(0);
        }
        else if(speed() > 0)
            currentPosition = 0.02 + height * Quad.easeInOut(prog, 0, 1, (float) duration);
        else if(speed() < 0)
            currentPosition = 0.02 + height - height * Quad.easeInOut(prog, 0, 1, (float) duration);


        return Math.abs(currentPosition);
    }

    public int getHeight(){
        int height = 0;
        for(WeakReference<TileEntityRolldoor> ref : elements){
            if(ref.get() != null && !ref.get().isInvalid())
                height = Math.max(ref.get().height(), height);
        }

        return height;
    }

    public void markDirtyClient() {
        markDirty();
        if (getWorld() != null) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
    }

    public int getWidth(){
        return elements.size();
    }

    public EnumFacing facing(){
        return facing;
    }

    private double speed(){
        return speed;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        elementsPos.clear();
        for(int i=0; nbt.hasKey("el"+i); i++)
            elementsPos.add(NBTUtil.getPosFromTag(nbt.getCompoundTag("el"+i)));

        facing = EnumFacing.values()[nbt.getInteger("facing")];
        speed = nbt.getDouble("speed");
        moveSpeed = nbt.getDouble("moveSpeed");
        currentPosition = nbt.getDouble("position");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        int i=0;
        for(BlockPos pos : elementsPos){
            nbt.setTag("el" + i, NBTUtil.createPosTag(pos));
            i++;
        }

        nbt.setInteger("facing", facing.ordinal());
        nbt.setDouble("speed", this.speed);
        nbt.setDouble("moveSpeed", this.moveSpeed);
        nbt.setDouble("position", this.currentPosition);

        return super.writeToNBT(nbt);
    }


    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

}
