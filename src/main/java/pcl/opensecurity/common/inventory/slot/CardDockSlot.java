package pcl.opensecurity.common.inventory.slot;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.driver.item.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.tileentity.TileEntityCardDock;

import javax.annotation.Nonnull;

public class CardDockSlot extends BaseSlot<TileEntityCardDock> {

    public CardDockSlot(TileEntityCardDock tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        DriverItem driver = Driver.driverFor(stack);
        return  driver != null && driver.slot(stack).equals(Slot.Card);
    }

}

