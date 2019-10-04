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
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.common.blocks.BlockRolldoorElement;
import pcl.opensecurity.common.interfaces.ICamo;
import pcl.opensecurity.common.tileentity.logic.RolldoorHelper;

import javax.annotation.Nullable;

import java.lang.ref.WeakReference;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;
import static pcl.opensecurity.common.blocks.BlockRolldoorElement.PROPERTYOFFSET;

public class TileEntityRolldoor extends TileEntityOSCamoBase implements ICamo {
    public final static int MAX_LENGTH = 15; // limited to 15 because of metaindex limits in the block

    private int height = 0;

    private AxisAlignedBB bb = FULL_BLOCK_AABB;

    private WeakReference<TileEntityRolldoorController> controller;
    private BlockPos origin;

    public TileEntityRolldoor() {
        super("os_" + BlockRolldoor.NAME);
    }

    public void remove(){
        removeElements();
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

        if(origin != null)
            nbt.setTag("origin", NBTUtil.createPosTag(origin));

        return super.writeToNBT(nbt);
    }

    public void initialize(){
        // check if block got placed next to a controller
        TileEntityRolldoorController controller = RolldoorHelper.getAdjacentController(getWorld(), getPos());

        // try to find the controller of adjacent rolldoor block
        if(controller == null){
            for(TileEntityRolldoor tile : RolldoorHelper.getAdjacentRolldoors(getWorld(), getPos()).values()){
                controller = tile.getController();
                if(controller != null)
                    break;
            }
        }

        // update if a controller was found
        if(controller != null)
            controller.initialize();

        updateHeight();
    }

    public EnumFacing getFacing(){
        return BlockRolldoor.getFacing(getWorld().getBlockState(getPos()));
    }

    public void updateHeight(){
        int oldHeight = height;
        height = 0;
        BlockPos pos = getPos().down();
        while((getWorld().isAirBlock(pos) || getWorld().getBlockState(pos).getBlock().equals(BlockRolldoorElement.DEFAULTITEM)) && height < MAX_LENGTH) {
            updateBlockState(pos);
            pos = pos.down();
            height++;
        }

        if(oldHeight == height) // dont schedule block update if the height didnt change
            return;

        updateBB();
        markDirtyClient();
    }

    private void updateBlockState(BlockPos position){
        IBlockState state = BlockRolldoorElement.DEFAULTITEM.getDefaultState();
        state = state.withProperty(PROPERTYOFFSET, height());
        getWorld().setBlockState(position, state);
    }

    private void updateBB(){
        bb = FULL_BLOCK_AABB.expand(0, -height(), 0);
    }

    private void removeElements(){
        height = 0;
        int element = 0;
        BlockPos pos = getPos().down();
        while((getWorld().isAirBlock(pos) || getWorld().getBlockState(pos).getBlock().equals(BlockRolldoorElement.DEFAULTITEM)) && element < MAX_LENGTH) {
            if(getWorld().getBlockState(pos).getBlock().equals(BlockRolldoorElement.DEFAULTITEM)){
                getWorld().setBlockToAir(pos);
            }
            pos = pos.down();
            element++;
        }
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
        if (origin == null)
            return null;

        if(controller == null || controller.get() == null || controller.get().isInvalid()) {
            TileEntity tile = getWorld().getTileEntity(origin);
            if(tile instanceof TileEntityRolldoorController)
                controller = new WeakReference<>((TileEntityRolldoorController) tile);
            else
                return null;
        }

        return controller.get();
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
