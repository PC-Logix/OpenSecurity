package pcl.opensecurity.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockOSBase;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.util.IOwner;
import pcl.opensecurity.util.IPasswordProtected;
import pcl.opensecurity.util.RolldoorHelper;

import java.util.UUID;

import static net.minecraft.block.Block.FULL_BLOCK_AABB;

public class TileEntityRolldoor extends TileEntityOSBase implements IOwner, IPasswordProtected {
    public final static int MAX_LENGTH = 16;

    private UUID ownerUUID;
    private String password = "";

    private int height = 0;
    private double currentPosition = 0;
    private double speed = 0;

    private AxisAlignedBB bb = FULL_BLOCK_AABB;

    BlockPos origin;

    public TileEntityRolldoor() {
        super("os_" + BlockRolldoor.NAME);
    }

    @Override
    public void update(){
        super.update();

        if(speed == 0)
            return;
    }

    public void remove(){
        removeElements();
        TileEntityRolldoorController controller = getController();
        if (controller != null)
            controller.removeElement(getPos());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        if(nbt.hasUniqueId("owner"))
            ownerUUID = nbt.getUniqueId("owner");
        else
            ownerUUID = null;

        password = nbt.getString("password");

        height = nbt.getInteger("height");
        speed = nbt.getDouble("speed");
        currentPosition = nbt.getDouble("position");

        if(nbt.hasKey("origin"))
            origin = NBTUtil.getPosFromTag(nbt.getCompoundTag("origin"));

        updateBB();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        if(ownerUUID != null)
            nbt.setUniqueId("owner", this.ownerUUID);

        nbt.setString("password", this.password);
        nbt.setInteger("height", this.height);
        nbt.setDouble("speed", this.speed);
        nbt.setDouble("position", this.currentPosition);

        if(origin() != null)
            nbt.setTag("origin", NBTUtil.createPosTag(origin()));

        return super.writeToNBT(nbt);
    }

    public void initialize(){
        TileEntityRolldoor te = RolldoorHelper.getAdjacentRolldoor(this);
            if(te != null && te.origin() != null){
            ((TileEntityRolldoorController) getWorld().getTileEntity(te.origin())).addElement(getPos());
        }

        updateHeight();
    }

    public EnumFacing getFacing(){
        return BlockOSBase.getFacing(getWorld().getBlockState(getPos()));
    }

    @Override
    public void invalidate(){
        super.invalidate();
    }

    public void open(){
        speed = -0.1;
    }

    public void close(){
        updateHeight();
        speed = 0.1;
    }

    public void toggle(){
        speed*=-1;
    }

    public void updateHeight(){
        height = 0;
        BlockPos pos = getPos().down();
        while((getWorld().isAirBlock(pos) || getWorld().getBlockState(pos).getBlock().equals(ContentRegistry.rolldoorElement)) && height < MAX_LENGTH) {
            if(getWorld().isAirBlock(pos)){
                getWorld().setBlockState(pos, ContentRegistry.rolldoorElement.getDefaultState());
                TileEntityRolldoorElement element = (TileEntityRolldoorElement) getWorld().getTileEntity(pos);
                element.setFacing(getFacing());
                element.setOrigin(getPos());
                element.setPosition(height);
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
        if(origin() != null)
            return getController().getRenderBoundingBox();
        else
            return getElementsBoundingBox();
    }

    public int height(){
        return height;
    }

    public AxisAlignedBB getElementsBoundingBox(){
        return bb;
    }

    public void setOwner(UUID uuid){
        ownerUUID = uuid;
    }

    public UUID getOwner(){
        return ownerUUID;
    }

    public void setPassword(String pass){
        password = pass;
    }

    public String getPass(){ return password; }

    public boolean isOpen(){
        return speed != 0 && currentPosition == height;
    }

    public void setOrigin(BlockPos pos){
        this.origin = pos;
    }

    public TileEntityRolldoorController getController(){
        if(origin() == null)
            return null;

        TileEntity tile = getWorld().getTileEntity(origin());
        return tile instanceof TileEntityRolldoorController ?  (TileEntityRolldoorController) tile : null;
    }

    public BlockPos origin(){
        return origin;
    }

}
