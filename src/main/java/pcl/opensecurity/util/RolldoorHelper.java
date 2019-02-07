package pcl.opensecurity.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;

import java.util.HashMap;

public class RolldoorHelper {
    public static HashMap<BlockPos, TileEntityRolldoor> getDoors(World world, BlockPos pos) {
        return getDoors(world, pos, true);
    }

    // scans the blockposition for any surrounding doors, if it found a door it will start a scan for a neighbourdoor
    // so dont parse the blockposition of any door to this!
    private static HashMap<BlockPos, TileEntityRolldoor> getDoors(World world, BlockPos pos, boolean searchMaindoor) {
        HashMap<BlockPos, TileEntityRolldoor> doors = new HashMap<>();

        for (EnumFacing direction : EnumFacing.VALUES) {
            if(!searchMaindoor && (direction.equals(EnumFacing.UP) || direction.equals(EnumFacing.DOWN)))
                continue;

            BlockPos position = pos.offset(direction); // Offset the block's position by 1 block in the current direction

            TileEntity tileEntity = world.getTileEntity(position); // Get the IBlockState's Block
            if (tileEntity instanceof TileEntityRolldoor) {
                doors.put(position, (TileEntityRolldoor) tileEntity);

                //if we found a door, we are making another loop with the last parameter set to false
                // otherwise we end in an loop where the doors find each other infinitely
                if(searchMaindoor)
                    doors.putAll(getDoors(world, position, false));
            }
        }

        return doors;
    }
}
