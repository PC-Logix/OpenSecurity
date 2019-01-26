package pcl.opensecurity.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class DoorHelper {
    public static HashMap<BlockPos, BlockDoor> getDoors(World world, BlockPos pos) {
        return getDoors(world, pos, true);
    }

    // scans the blockposition for any surrounding doors, if it found a door it will start a scan for a neighbourdoor
    // so dont parse the blockposition of any door to this!
    private static HashMap<BlockPos, BlockDoor> getDoors(World world, BlockPos pos, boolean searchMaindoor) {
        HashMap<BlockPos, BlockDoor> doors = new HashMap<>();

        for (EnumFacing direction : EnumFacing.VALUES) {
            if(!searchMaindoor && (direction.equals(EnumFacing.UP) || direction.equals(EnumFacing.DOWN)))
                continue;

            BlockPos position = pos.offset(direction); // Offset the block's position by 1 block in the current direction

            Block block = world.getBlockState(position).getBlock(); // Get the IBlockState's Block
            if (block instanceof BlockDoor) {
                doors.put(position, (BlockDoor) block);

                //if we found a door, we are making another loop with the last parameter set to false
                // otherwise we end in an loop where the doors find each other infinitely
                if(searchMaindoor)
                    doors.putAll(getDoors(world, position, false));
            }
        }

        return doors;
    }

}
