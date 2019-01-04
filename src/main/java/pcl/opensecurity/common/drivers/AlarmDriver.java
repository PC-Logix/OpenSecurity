package pcl.opensecurity.common.drivers;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Tier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.blocks.BlockAlarm;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;

public class AlarmDriver extends BlockAlarm implements DriverItem, EnvironmentProvider, HostAware {
    public static AlarmDriver driver = new AlarmDriver();

    @Override
    public boolean worksWith(ItemStack stack) {
        return stack.getItem().equals(ContentRegistry.alarmItem);
    }

    @Override
    public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
        if(!worksWith(stack))
            return false;

        return true;
    }

    @Override
    public Class<? extends Environment> getEnvironment(ItemStack stack) {
        return worksWith(stack) ? TileEntityAlarm.class : null;
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container) {
        return new TileEntityAlarm(container);
    }

    @Override
    public String slot(ItemStack stack){
        return Slot.Upgrade;
    }

    @Override
    public int tier(ItemStack stack) {
        return Tier.One();
    }

    @Override
    public NBTTagCompound dataTag(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        final NBTTagCompound nbt = stack.getTagCompound();
        // This is the suggested key under which to store item component data.
        // You are free to change this as you please.
        if(!nbt.hasKey("oc:data")) {
            nbt.setTag("oc:data", new NBTTagCompound());
        }
        return nbt.getCompoundTag("oc:data");
    }
}