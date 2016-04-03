package pcl.opensecurity.items;

import pcl.opensecurity.drivers.SecureNetworkCardDriver;
import li.cil.oc.api.driver.EnvironmentAware;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.item.HostAware;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemSecureNetworkCard extends Item implements li.cil.oc.api.driver.Item, HostAware, EnvironmentAware {
	public ItemSecureNetworkCard() {
		super();
		setUnlocalizedName("secureNetworkCard");
		setTextureName("opensecurity:rfidReaderCard");
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(this);
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
		return new SecureNetworkCardDriver(host);
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Card;
	}

	@Override
	public int tier(ItemStack stack) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public NBTTagCompound dataTag(ItemStack stack) {
		if(!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound nbt = stack.getTagCompound();
		if(!nbt.hasKey("oc:data")) {
			nbt.setTag("oc:data", new NBTTagCompound());
		}
		return nbt.getCompoundTag("oc:data");
	}

	@Override
	public Class<? extends Environment> providedEnvironment(ItemStack stack) {
		// TODO Auto-generated method stub
		return SecureNetworkCardDriver.class;
	}

	@Override
	public boolean worksWith(ItemStack stack, Class<? extends EnvironmentHost> host) {
		// TODO Auto-generated method stub
		boolean works = worksWith(stack);
		return works;
	}
}
