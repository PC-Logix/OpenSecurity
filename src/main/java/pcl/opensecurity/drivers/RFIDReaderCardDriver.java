package pcl.opensecurity.drivers;

import pcl.opensecurity.OpenSecurity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverItem;
import li.cil.oc.common.item.TabletWrapper;

public class RFIDReaderCardDriver extends DriverItem {

	public RFIDReaderCardDriver() {
		super(new ItemStack(OpenSecurity.rfidReaderCard));
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container)
	{
		if (container instanceof TileEntity)
			return new Environment((TileEntity) container);
		if (container instanceof TabletWrapper)
			return new Environment((TabletWrapper) container);
		return null;
	}

	@Override
	public String slot(ItemStack stack)
	{
		return Slot.Card;
	}

	public class Environment extends li.cil.oc.api.prefab.ManagedEnvironment {
		protected TileEntity container = null;
		protected TabletWrapper container2 = null;
		
		public Environment(TileEntity container) {
			this.container = container;
			this.setNode(Network.newNode(this, Visibility.Neighbors).withComponent("RFIDReaderCard").create());
		}


		public Environment(TabletWrapper container) {
			this.container2 = container;
			this.setNode(Network.newNode(this, Visibility.Neighbors).withComponent("RFIDReaderCard").create());
		}


		@Callback
		public Object[] scan(Context context, Arguments args)
		{
			System.out.println("Hai");
			return new Object[] { "completed" };
		}
		
		@Callback
		public Object[] send(Context context, Arguments args)
		{
			return new Object[] { "completed" };
		}
	}
}
