package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.common.component.RolldoorController;
import pcl.opensecurity.util.RolldoorHelper;
import scala.Int;

import java.util.ArrayList;
import java.util.HashSet;

public class TileEntityRolldoorController extends TileEntityDoorController {
    public TileEntityRolldoorController(){
        super("os_rolldoorcontroller");
    }

    HashSet<BlockPos> elements = new HashSet<>();

    @Override
    public void validate(){
        super.validate();
    }

    public void initialize(){
        for(BlockPos pos : RolldoorHelper.getDoors(this).keySet())
            addElement(pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        super.readFromNBT(nbt);

        elements.clear();
        for(int i=0; nbt.hasKey("el"+i); i++){
            NBTTagCompound tag = nbt.getCompoundTag("el"+i);
            elements.add(new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z")));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        int i=0;
        for(BlockPos pos : elements){
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", pos.getX());
            tag.setInteger("y", pos.getY());
            tag.setInteger("z", pos.getZ());

            nbt.setTag("el"+i, tag);
            i++;
        }

        return super.writeToNBT(nbt);
    }

    @Override
    public void invalidate(){
        HashSet<BlockPos> foo = new HashSet<>();
        foo.addAll(elements);

        for(BlockPos pos : foo)
            removeElement(pos);

        super.invalidate();
    }

    public void addElement(BlockPos pos){
        ((TileEntityRolldoor) getWorld().getTileEntity(pos)).setOrigin(getPos());
        elements.add(pos);
    }

    public void removeElement(BlockPos pos){
        TileEntity tile = getWorld().getTileEntity(pos);

        if(tile instanceof TileEntityRolldoor)
            ((TileEntityRolldoor) tile).setOrigin(null);

        elements.remove(pos);
    }

    // OC Callbacks
    @Callback
    @Override
    public Object[] isOpen(Context context, Arguments args) {
        return RolldoorController.isOpen(this);
    }

    @Callback
    @Override
    public Object[] toggle(Context context, Arguments args) {
        return RolldoorController.toggle(this, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] open(Context context, Arguments args) {
        return RolldoorController.setDoorStates(this, true, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] close(Context context, Arguments args) {
        return RolldoorController.setDoorStates(this, false, args.optString(0, ""));
    }

    @Callback
    @Override
    public Object[] removePassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(this, args.checkString(0), "");
    }

    @Callback
    @Override
    public Object[] setPassword(Context context, Arguments args) {
        return RolldoorController.setDoorPasswords(this, args.checkString(0), args.checkString(1));
    }

    @Callback
    public Object[] getHeight(Context context, Arguments args) {
        ArrayList<Integer> heights = new ArrayList<>();

        for(BlockPos pos : elements){
            TileEntity tile = getWorld().getTileEntity(pos);
            if(tile instanceof TileEntityRolldoor)
                heights.add(((TileEntityRolldoor) tile).height());
        }

        return new Object[]{ heights.toArray() };
    }

}
