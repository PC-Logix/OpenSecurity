package pcl.opensecurity.common.tileentity;

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
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockOSBase;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.util.RolldoorHelper;

import javax.annotation.Nullable;

import java.lang.ref.WeakReference;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;
import static pcl.opensecurity.common.blocks.BlockRolldoorElement.PROPERTYOFFSET;

public class TileEntityRolldoor extends TileEntityOSBase {
    public final static int MAX_LENGTH = 16; // limited to 16 because of metaindex limits in the block

    private int height = 0;

    private AxisAlignedBB bb = FULL_BLOCK_AABB;

    WeakReference<TileEntityRolldoorController> controller;

    BlockPos origin;

    public TileEntityRolldoor() {
        super("os_" + BlockRolldoor.NAME);
    }

    public void remove(){
        removeElements();
        TileEntityRolldoorController controller = getController();
        if (controller != null)
            controller.initialize();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        height = nbt.getInteger("height");

        if(nbt.hasKey("origin"))
            origin = NBTUtil.getPosFromTag(nbt.getCompoundTag("origin"));

        updateBB();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("height", this.height);

        if(origin() != null)
            nbt.setTag("origin", NBTUtil.createPosTag(origin()));

        return super.writeToNBT(nbt);
    }

    public void initialize(){
        // check if block got placed next to a controller
        TileEntityRolldoorController controller = RolldoorHelper.getAdjacentController(getWorld(), getPos());

        // try to find the controller of adjacent rolldoor block
        if(controller == null){
            TileEntityRolldoor te = RolldoorHelper.getAdjacentRolldoor(this);
            if(te != null)
                controller = te.getController();
        }

        // update if a controller was found
        if(controller != null)
            controller.initialize();

        updateHeight();
    }

    public EnumFacing getFacing(){
        return BlockOSBase.getFacing(getWorld().getBlockState(getPos()));
    }

    public void updateHeight(){
        height = 0;
        BlockPos pos = getPos().down();
        while((getWorld().isAirBlock(pos) || getWorld().getBlockState(pos).getBlock().equals(ContentRegistry.rolldoorElement)) && height < MAX_LENGTH) {
            if(getWorld().isAirBlock(pos)){
                IBlockState state = ContentRegistry.rolldoorElement.getDefaultState();
                state = state.withProperty(PROPERTYOFFSET, height);
                getWorld().setBlockState(pos, state);
            }
            pos = pos.down();
            height++;
        }
        updateBB();
    }

    public void updateBB(){
        bb = FULL_BLOCK_AABB.expand(0, -height(), 0);
    }

    public void removeElements(){
        height = 0;
        BlockPos pos = getPos().down();
        while((getWorld().isAirBlock(pos) || getWorld().getBlockState(pos).getBlock().equals(ContentRegistry.rolldoorElement)) && height < MAX_LENGTH) {
            if(getWorld().getBlockState(pos).getBlock().equals(ContentRegistry.rolldoorElement)){
                getWorld().setBlockToAir(pos);
            }
            pos = pos.down();
            height++;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox(){

        /*
        if(origin() != null)
            return getController().getRenderBoundingBox();
        else
            return getElementsBoundingBox();
        */

        return super.getRenderBoundingBox();
    }

    public int height(){
        return height;
    }

    public AxisAlignedBB getElementsBoundingBox(){
        return bb.offset(getPos());
    }

    public void setOrigin(BlockPos pos){
        this.origin = pos;
    }

    public TileEntityRolldoorController getController(){
        if(controller == null || controller.get() == null || controller.get().isInvalid()) {
            if (origin() == null)
                return null;

            TileEntity tile = getWorld().getTileEntity(origin());
            if(tile instanceof TileEntityRolldoorController)
                controller = new WeakReference<>((TileEntityRolldoorController) tile);
            else
                return null;
        }

        return controller.get();
    }

    public BlockPos origin(){
        return origin;
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
