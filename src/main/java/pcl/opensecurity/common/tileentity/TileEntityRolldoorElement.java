package pcl.opensecurity.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.util.AABBHelper;

public class TileEntityRolldoorElement extends TileEntity {
    AxisAlignedBB bb = new AxisAlignedBB(0, 0, 1f/16 * 6, 1, 1, 1f - 1f/16 * 6);
    EnumFacing facing = EnumFacing.NORTH;
    BlockPos origin;
    int position = 0;

    public AxisAlignedBB getBoundingBox(){
        if(getRolldoor() != null) {
            TileEntityRolldoorController controller = getRolldoor().getController();

            if (controller != null) {
                AxisAlignedBB bbMod = bb;
                double height = controller.getCurrentHeight() - position;

                if (height < 0)
                    bbMod = BlockRolldoor.emptyBB;
                else if (height < 1) {
                    bbMod = bbMod.intersect(new AxisAlignedBB(0, 1f-height, 0, 1, 1, 1));
                }

                return AABBHelper.rotateHorizontal(bbMod, facing);
            }
        }
        return AABBHelper.rotateHorizontal(bb, facing);
    }

    private TileEntityRolldoor getRolldoor(){
        if(origin() == null)
            return null;

        TileEntity tile = getWorld().getTileEntity(origin());

        return tile instanceof TileEntityRolldoor ? (TileEntityRolldoor) tile : null;
    }

    public void setFacing(EnumFacing enumFacing){
        facing = enumFacing;
    }

    public void setOrigin(BlockPos rolldoor){
        origin = rolldoor;
    }

    public void setPosition(int pos){
        position = pos;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);
        facing = EnumFacing.values()[nbt.getInteger("facing")];
        if(nbt.hasKey("origin"))
            origin = NBTUtil.getPosFromTag(nbt.getCompoundTag("origin"));

        position = nbt.getInteger("pos");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setInteger("facing", facing.ordinal());
        if(origin() != null)
            nbt.setTag("origin", NBTUtil.createPosTag(origin()));
        nbt.setInteger("pos", position);
        return super.writeToNBT(nbt);
    }

    public BlockPos origin(){
        return origin;
    }
}
