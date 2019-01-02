package pcl.opensecurity.common.component;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockSecureDoor;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;
import pcl.opensecurity.util.DoorHelper;

import java.util.ArrayList;
import java.util.Map;

public class DoorController {

    static public Object[] setDoorPasswords(World world, BlockPos controller, String oldPass, String newPass){
        ArrayList<Object[]> doorResponses = new ArrayList<>();
        for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(world, controller).entrySet()){
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
        for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(world, controller).entrySet()){
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
            return doorResponses.toArray();
    }

    static public Object[] isOpen(World world, BlockPos controller) {
        ArrayList<Boolean> states = new ArrayList<>();

        for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(world, controller).entrySet())
            states.add(BlockDoor.isOpen(world, doorSet.getKey()));

        return new Object[] { states.toArray() };
    }

    static public Object[] toggle(World world, BlockPos controller, String password) {
        for(Map.Entry<BlockPos, BlockDoor> doorSet : DoorHelper.getDoors(world, controller).entrySet()){
            // we can return after the first hit as the method call toggles all the doors again anyways
            return setDoorStates(world, controller, !BlockDoor.isOpen(world, doorSet.getKey()), password);
        }

        return new Object[]{ false, "couldnt find any door" };
    }

}
