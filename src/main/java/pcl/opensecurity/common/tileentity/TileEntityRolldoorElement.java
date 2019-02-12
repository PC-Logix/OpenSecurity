package pcl.opensecurity.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.common.blocks.BlockRolldoorElement;
import pcl.opensecurity.util.AABBHelper;

import java.lang.ref.WeakReference;

public class TileEntityRolldoorElement extends TileEntity {
    private static final AxisAlignedBB bbDefault = new AxisAlignedBB(0, 0, 1f/16 * 6, 1, 1, 1f - 1f/16 * 6);
    private AxisAlignedBB bb = bbDefault;
    private EnumFacing facing;
    private int position = 0;

    WeakReference<TileEntityRolldoor> rolldoor;

    public AxisAlignedBB getBoundingBox(){
        getFacing(); //call this to update the local BB when necessary

        TileEntityRolldoor rolldoor = getRolldoor();
        if(rolldoor == null)
            return BlockRolldoor.emptyBB;

        TileEntityRolldoorController controller = rolldoor.getController();
        if (controller == null)
            return BlockRolldoor.emptyBB;

        /* ^^ no rolldoor or no controller => no bounding box */

        double height = controller.getCurrentHeight() - getPosition();

        if (height < 0)
            return BlockRolldoor.emptyBB;
        else if (height < 1)
            return bb.intersect(new AxisAlignedBB(0, 1f-height, 0, 1, 1, 1));
        else
            return bb;
    }

    private TileEntityRolldoor getRolldoor(){
        if(rolldoor == null || rolldoor.get() == null || rolldoor.get().isInvalid()) {
            if (origin() == null)
                return null;

            TileEntity tile = getWorld().getTileEntity(origin());
            if(tile instanceof TileEntityRolldoor)
                rolldoor = new WeakReference<>((TileEntityRolldoor) tile);
            else
                return null;
        }

        return rolldoor.get();
    }

    public BlockPos origin(){
        return getPos().add(0, getPosition() + 1, 0);
    }

    public int getPosition(){
        if(position == 0){
            position = BlockRolldoorElement.getOffset(getWorld().getBlockState(getPos()));
        }
        return position;
    }

    public EnumFacing getFacing(){
        if(facing == null && origin() != null && getWorld() != null) {
            facing = BlockRolldoor.getFacing(getWorld().getBlockState(origin()));
            bb = AABBHelper.rotateHorizontal(bbDefault, facing);
        }
        return facing;
    }

}
