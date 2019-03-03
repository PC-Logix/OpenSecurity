package pcl.opensecurity.common.drivers;

import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.items.ItemRFIDReaderCard;
import pcl.opensecurity.common.tileentity.TileEntityRFIDReader;

public class RFIDReaderCardDriver extends DriverItem {
	public static RFIDReaderCardDriver driver = new RFIDReaderCardDriver();

	public RFIDReaderCardDriver() {
		super(ItemRFIDReaderCard.DEFAULTSTACK);
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container) {
		return new TileEntityRFIDReader(container);
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Card;
	}

}