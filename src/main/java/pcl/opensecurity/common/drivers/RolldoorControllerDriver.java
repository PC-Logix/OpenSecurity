package pcl.opensecurity.common.drivers;

import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.EnvironmentProvider;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.internal.Microcontroller;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.common.Tier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import pcl.opensecurity.common.blocks.BlockRolldoorController;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class RolldoorControllerDriver extends BlockRolldoorController implements DriverItem, EnvironmentProvider, HostAware {
    public static RolldoorControllerDriver driver = new RolldoorControllerDriver();

    @Override
    public boolean worksWith(ItemStack stack) {
        return stack.getItem().equals(Item.getItemFromBlock(BlockRolldoorController.DEFAULTITEM));
    }

    @Override
    public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
        return worksWith(stack) && Microcontroller.class.isAssignableFrom(host);
    }

    @Override
    public Class<? extends Environment> getEnvironment(ItemStack stack) {
        return worksWith(stack) ? TileEntityRolldoorController.class : null;
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container) {
        TileEntityRolldoorController controller = new TileEntityRolldoorController(container);
        controller.initialize();
        return controller;
    }

    @Override
    public String slot(ItemStack stack) {
        return Slot.Upgrade;
    }

    @Override
    public int tier(ItemStack stack) {
        return Tier.One();
    }

    @Override
    public NBTTagCompound dataTag(ItemStack stack) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        final NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey("oc:data"))
            nbt.setTag("oc:data", new NBTTagCompound());

        return nbt.getCompoundTag("oc:data");
    }
}
