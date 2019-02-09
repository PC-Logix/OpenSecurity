package pcl.opensecurity.common.component;

import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;
import pcl.opensecurity.util.RolldoorHelper;

import java.util.ArrayList;
import java.util.Map;

public class RolldoorController {
    //this was outsourced as i thought that would be needed for the upgrade, could be merged back, but however...
    static public Object[] setDoorPasswords(TileEntityRolldoorController controller, String oldPass, String newPass){
        ArrayList<Object[]> doorResponses = new ArrayList<>();
        for(TileEntityRolldoor te : RolldoorHelper.getDoors(controller).values()){
            if (te.getPass().isEmpty()) {
                te.setPassword(newPass);
                doorResponses.add(new Object[] { true, "Password set" });
            } else {
                if (oldPass.equals(te.getPass())) {
                    te.setPassword(newPass);
                    doorResponses.add(new Object[] { true, "Password Changed" });
                } else {
                    doorResponses.add(new Object[] { false, "Password was not changed" });
                }
            }
        }

        if(doorResponses.size() == 0)
            return new Object[] { false, "No Rolldoor found" };
        else
            return new Object[] { doorResponses.toArray() };
    }

    public static Object[] setDoorStates(TileEntityRolldoorController controller, boolean open, String password){
        ArrayList<Object[]> doorResponses = new ArrayList<>();
        for(TileEntityRolldoor te : RolldoorHelper.getDoors(controller).values()){
            if (password.equals(te.getPass())) {
                te.toggle();
                doorResponses.add(new Object[] { true });
            } else {
                doorResponses.add(new Object[] { false, "Password incorrect" });
            }
        }

        if(doorResponses.size() == 0)
            return new Object[] { false, "No Rolldoor found" };
        else
            return new Object[] { doorResponses.toArray() };
    }

    static public Object[] isOpen(TileEntityRolldoorController controller) {
        ArrayList<Boolean> states = new ArrayList<>();

        for(Map.Entry<BlockPos, TileEntityRolldoor> doorSet : RolldoorHelper.getDoors(controller).entrySet())
            states.add(doorSet.getValue().isOpen());

        return new Object[] { states.toArray() };
    }

    static public Object[] toggle(TileEntityRolldoorController controller, String password) {
        for(Map.Entry<BlockPos, TileEntityRolldoor> doorSet : RolldoorHelper.getDoors(controller).entrySet()){
            // we can return after the first hit as the method call toggles all the doors again anyways
            return setDoorStates(controller, !doorSet.getValue().isOpen(), password);
        }

        return new Object[]{ false, "couldnt find any rolldoor" };
    }

}
