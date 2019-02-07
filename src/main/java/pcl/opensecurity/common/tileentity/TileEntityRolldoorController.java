package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import pcl.opensecurity.common.component.RolldoorController;

public class TileEntityRolldoorController extends TileEntityDoorController {
    public TileEntityRolldoorController(){
        super("os_rolldoorcontroller");
    }

    // OC Callbacks
    @Callback
    @Override
    public Object[] isOpen(Context context, Arguments args) {
        return RolldoorController.isOpen(getWorld(), getPos());
    }

    @Callback
    @Override
    public Object[] toggle(Context context, Arguments args) {
        return RolldoorController.toggle(getWorld(), getPos(), args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] open(Context context, Arguments args) {
        return RolldoorController.setDoorStates(getWorld(), getPos(), true, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] close(Context context, Arguments args) {
        return RolldoorController.setDoorStates(getWorld(), getPos(), false, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] removePassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), "");
    }

    @Callback
    @Override
    public Object[] setPassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), args.checkString(1));
    }

}
