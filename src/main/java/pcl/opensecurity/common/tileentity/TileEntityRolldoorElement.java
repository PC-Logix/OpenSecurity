package pcl.opensecurity.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.blocks.BlockRolldoor;
import pcl.opensecurity.common.blocks.BlockRolldoorElement;
import pcl.opensecurity.util.AABBHelper;

import java.lang.ref.WeakReference;

import static pcl.opensecurity.common.blocks.BlockOSBase.PROPERTYFACING;
import static pcl.opensecurity.common.blocks.BlockRolldoorElement.PROPERTYOFFSET;

public class TileEntityRolldoorElement extends TileEntity {
    private static final AxisAlignedBB bbDefault = new AxisAlignedBB(0, 0, 1f/16 * 6, 1, 1, 1f - 1f/16 * 6);
    private AxisAlignedBB bb = bbDefault;
    private EnumFacing facing;
    private int position = -1;

    private WeakReference<TileEntityRolldoor> rolldoor;

    public AxisAlignedBB getBoundingBox(){
        getFacing(); //call this to update the local BB when necessary

        TileEntityRolldoor rolldoor = getRolldoor();
        if(rolldoor == null)
            return BlockRolldoorElement.emptyBB;

        TileEntityRolldoorController controller = rolldoor.getController();
        if (controller == null)
            return BlockRolldoorElement.emptyBB;

        /* ^^ no rolldoor or no controller => no bounding box */

        double height = controller.getCurrentHeight() - getPosition();

        if (height < 0)
            return BlockRolldoorElement.emptyBB;
        else if (height < 1)
            return bb.intersect(new AxisAlignedBB(0, 1f-height, 0, 1, 1, 1));
        else
            return bb;
    }

    private TileEntityRolldoor getRolldoor(){
        if(rolldoor == null || rolldoor.get() == null || rolldoor.get().isInvalid()) {
            TileEntity tile = getWorld().getTileEntity(origin());
            if(tile instanceof TileEntityRolldoor)
                rolldoor = new WeakReference<>((TileEntityRolldoor) tile);
            else
                return null;
        }

        return rolldoor.get();
    }

    private BlockPos origin(){
        return getPos().add(0, getPosition() + 1, 0);
    }

    private int getPosition(){
        if(position == -1){
            IBlockState state = getWorld().getBlockState(getPos());
            if(state.getProperties().containsKey(PROPERTYOFFSET))
                position = BlockRolldoorElement.getOffset(state);
        }
        return position;
    }

    public EnumFacing getFacing(){
        if(facing == null) {
            IBlockState state = getWorld().getBlockState(origin());
            if(state.getProperties().containsKey(PROPERTYFACING)) {
                facing = BlockRolldoor.getFacing(state);
                bb = AABBHelper.rotateHorizontal(bbDefault, facing);
            }
            else
                return EnumFacing.UP;
        }
        return facing;
    }

}
