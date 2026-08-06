package pcl.opensecurity.integration.opencomputers;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.world.item.ItemStack;

public final class RFIDReaderCardDriver extends DriverItem {
    public RFIDReaderCardDriver() {
        super(new ItemStack(OpenSecurity.RFID_READER_CARD.get()));
    }

    @Override
    public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
        return new RFIDReaderEnvironment(host);
    }

    @Override
    public String slot(ItemStack stack) {
        return Slot.Card;
    }

    @Override
    public int tier(ItemStack stack) {
        return 1;
    }
}
