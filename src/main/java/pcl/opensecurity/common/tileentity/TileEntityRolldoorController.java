package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.component.RolldoorController;
import pcl.opensecurity.util.RolldoorHelper;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
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

    private HashSet<BlockPos> elements = new HashSet<>();

    @Override
    public void update(){
        super.update();
        if(speed() != 0)
            getCurrentHeight();
    }

    // OC Callbacks
    @Callback
    @Override
    public Object[] isOpen(Context context, Arguments args) {
        return RolldoorController.isOpen(this);
    }

    @Callback
    @Override
    public Object[] toggle(Context context, Arguments args) {
        if(speed != 0)
            setSpeed(speed * -1);
        else if(currentPosition == 0)
            setSpeed(0.5);
        else
            setSpeed(-0.5);

        return RolldoorController.toggle(this, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] open(Context context, Arguments args) {
        setSpeed(-0.5);
        return RolldoorController.setDoorStates(this, true, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] close(Context context, Arguments args) {
        setSpeed(0.5);
        return RolldoorController.setDoorStates(this, false, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] removePassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(this, args.checkString(0), "");
    }

    @Callback
    @Override
    public Object[] setPassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(this, args.checkString(0), args.checkString(1));
    }

    @Callback
    public Object[] getHeight(Context context, Arguments args) {
        ArrayList<Integer> heights = new ArrayList<>();

        for(BlockPos pos : elements){
            TileEntity tile = getWorld().getTileEntity(pos);
            if(tile instanceof TileEntityRolldoor)
                heights.add(((TileEntityRolldoor) tile).height());
        }

        return new Object[]{ heights.toArray() };
    }


    public void initialize(){
        elements.clear();

        for(BlockPos pos : RolldoorHelper.getDoors(this).keySet())
            addElement(pos);
    }

    public void remove(){
        for(BlockPos pos : new HashSet<BlockPos>(Arrays.asList(elements.toArray())))
            removeElement(pos);
    }

    private TileEntityRolldoor getRolldoor(BlockPos pos){
        TileEntity tile = getWorld().getTileEntity(pos);
        return tile instanceof TileEntityRolldoor ? (TileEntityRolldoor) tile : null;
    }

    void addElement(BlockPos pos){
        TileEntityRolldoor tile = getRolldoor(pos);

        if(tile != null) {
            tile.setOrigin(getPos());
            facing = tile.getFacing();
        }

        elements.add(pos);
    }

    void removeElement(BlockPos pos){
        TileEntity tile = getWorld().getTileEntity(pos);

        if(tile instanceof TileEntityRolldoor)
            ((TileEntityRolldoor) tile).setOrigin(null);

        initialize();
    }

    private void setSpeed(double speedIn){
        speed = speedIn;
        markDirtyClient();
    }

    @Nonnull
    public AxisAlignedBB getRenderBoundingBox(){
        AxisAlignedBB bb = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

        for(BlockPos pos : elements){
            TileEntityRolldoor tile = (TileEntityRolldoor) getWorld().getTileEntity(pos);
            if(tile != null)
                bb.union(tile.getElementsBoundingBox());
        }
        return bb.offset(getPos());
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
            currentPosition = 0.02 + height * Math.abs(prog/duration);
        else if(speed() < 0)
            currentPosition = 0.02 + height - height * Math.abs(prog/duration);


        return Math.abs(currentPosition);
    }

    private int getHeight(){
        int height = 0;
        for(BlockPos pos : elements){
            TileEntity tile = getWorld().getTileEntity(pos);
            if(tile instanceof TileEntityRolldoor)
                height = Math.max(((TileEntityRolldoor) tile).height(), height);
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

        elements.clear();
        for(int i=0; nbt.hasKey("el"+i); i++)
            elements.add(NBTUtil.getPosFromTag(nbt.getCompoundTag("el"+i)));

        facing = EnumFacing.values()[nbt.getInteger("facing")];
        speed = nbt.getDouble("speed");
        currentPosition = nbt.getDouble("position");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        int i=0;
        for(BlockPos pos : elements){
            nbt.setTag("el"+i, NBTUtil.createPosTag(pos));
            i++;
        }

        nbt.setInteger("facing", facing.ordinal());
        nbt.setDouble("speed", this.speed);
        nbt.setDouble("position", this.currentPosition);

        return super.writeToNBT(nbt);
    }

}
