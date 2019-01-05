package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.common.component.ManagedComponent;
import pcl.opensecurity.common.component.ManagedComponentHost;
import pcl.opensecurity.util.ItemUtils;

import javax.annotation.Nonnull;

public class TileEntityCardDock extends TileEntityOSBase implements ManagedComponentHost {
    ItemStackHandler inventory;

    ManagedComponent component = new ManagedComponent(this);

    public TileEntityCardDock() {
        super("os_carddock");
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

        inventory = new ItemStackHandler(1){
            @Override
            public void onContentsChanged(int slot){
                if(getWorld().isRemote)
                    return;

                component.set(getStackInSlot(slot));
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }


    @Override
    public void update(){
        super.update();
        component.update();
    }

    @Callback(doc = "function():array; returns the address, type of component in the dock and the address, machine its bound to", direct = true)
    public Object[] getComponent(Context context, Arguments args) {
        if(component.getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(component.node() == null)
            return new Object[]{ false, "no environment available" };

        return new Object[]{
                new Object[]{
                        new Object[]{ component.node().address(), component.node().toString()},
                        new Object[]{ component.getBoundMachine().address(), component.getBoundMachine()}} };
    }

    @Callback(doc = "function():boolean; binds the component in the dock to the machine that issues this command", direct = true)
    public Object[] bindComponent(Context context, Arguments args) {
        if(component.getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(component.node() == null)
            return new Object[]{ false, "no environment available" };

        if(component.getBoundMachine() != null){
            if(component.getBoundMachine().equals(context.node()))
                return new Object[]{ false, "component already bound to this machine" };
            else
                return new Object[]{ false, "component already bound to another machine" };
        }

        component.bindMachine(context.node());

        return new Object[]{ true };
    }

    @Callback(doc = "function():boolean; unbinds the component in the dock, this can only be used by the machine the component is bound to", direct = true)
    public Object[] unbindComponent(Context context, Arguments args) {
        if(component.getComponentItem().isEmpty())
            return new Object[]{ false, "no component in card dock" };

        if(component.getBoundMachine() == null)
            return new Object[]{ false, "component isnt bound to any machine" };

        if(!component.getBoundMachine().equals(context.node()))
            return new Object[]{ false, "component bound to another machine" };

        component.unbindMachine(context.node());

        return new Object[]{ true };
    }


    @Override
    public void onConnect(Node arg0) {
        super.onConnect(arg0);
        component.onConnect(arg0);
    }


    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        inventory.deserializeNBT(data.getCompoundTag("inventory"));
        component.readFromNBT(data.getCompoundTag("component"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data.setTag("inventory", inventory.serializeNBT());
        data.setTag("component", component.writeToNBT(new NBTTagCompound()));

        return super.writeToNBT(data);
    }


    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.UP) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventory;
        }

        return super.getCapability(capability, facing);
    }


    public void removed(){
        component.disconnect();

        // drop inventory
        for(int slot=0; slot < inventory.getSlots(); slot++)
            ItemUtils.dropItem(inventory.getStackInSlot(slot), getWorld(), getPos(), false, 10);
    }


    // Environment Host
    @Override
    public World world(){ return getWorld(); }

    @Override
    public double xPosition(){ return getPos().getX(); }

    @Override
    public double yPosition(){ return getPos().getY(); }

    @Override
    public double zPosition(){ return getPos().getZ(); }

    @Override
    public void markChanged(){}


}
