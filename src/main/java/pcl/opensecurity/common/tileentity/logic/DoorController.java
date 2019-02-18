package pcl.opensecurity.common.tileentity.logic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockSecureDoor;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoorController {
    //this was outsourced as i thought that would be needed for the upgrade, could be merged back, but however...
    static public Object[] setDoorPasswords(World world, BlockPos controller, String oldPass, String newPass){
        ArrayList<Object[]> doorResponses = new ArrayList<>();
        for(Map.Entry<BlockPos, BlockDoor> doorSet : getDoors(world, controller).entrySet()){
            if(!(doorSet.getValue() instanceof BlockSecureDoor))
                continue;

            TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorSet.getKey());
            TileEntitySecureDoor otherTE = (TileEntitySecureDoor) world.getTileEntity(BlockSecureDoor.getOtherDoorPart(world, doorSet.getKey()));
            if (te.getPass().isEmpty()) {
                te.setPassword(newPass);
                otherTE.setPassword(newPass);
                doorResponses.add(new Object[] { true, "Password set" });
            } else {
                if (oldPass.equals(te.getPass())) {
                    te.setPassword(newPass);
                    otherTE.setPassword(newPass);
                    doorResponses.add(new Object[] { true, "Password Changed" });
                } else {
                    doorResponses.add(new Object[] { false, "Password was not changed" });
                }
            }
        }

        if(doorResponses.size() == 0)
            return new Object[] { false, "No Security door found" };
        else
            return new Object[] { doorResponses.toArray() };
    }

    public static Object[] setDoorStates(World world, BlockPos controller, boolean open, String password){
        ArrayList<Object[]> doorResponses = new ArrayList<>();
        for(Map.Entry<BlockPos, BlockDoor> doorSet : getDoors(world, controller).entrySet()){
            if(doorSet.getValue() instanceof BlockSecureDoor) {
                TileEntitySecureDoor te = (TileEntitySecureDoor) world.getTileEntity(doorSet.getKey());
                if (password.equals(te.getPass())) {
                    doorSet.getValue().toggleDoor(world, doorSet.getKey(), open);
                    doorResponses.add(new Object[] { true });
                } else {
                    doorResponses.add(new Object[] { false, "Password incorrect" });
                }
            }
            else {
                doorSet.getValue().toggleDoor(world, doorSet.getKey(), open);
                doorResponses.add(new Object[] { true });
            }
        }

        if(doorResponses.size() == 0)
            return new Object[] { false, "No Security door found" };
        else
            return new Object[] { doorResponses.toArray() };
    }

    static public Object[] isOpen(World world, BlockPos controller) {
        ArrayList<Boolean> states = new ArrayList<>();

        for(Map.Entry<BlockPos, BlockDoor> doorSet : getDoors(world, controller).entrySet())
            states.add(BlockDoor.isOpen(world, doorSet.getKey()));

        return new Object[] { states.toArray() };
    }

    static public Object[] toggle(World world, BlockPos controller, String password) {
        for(Map.Entry<BlockPos, BlockDoor> doorSet : getDoors(world, controller).entrySet()){
            // we can return after the first hit as the method call toggles all the doors again anyways
            return setDoorStates(world, controller, !BlockDoor.isOpen(world, doorSet.getKey()), password);
        }

        return new Object[]{ false, "couldnt find any door" };
    }


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
