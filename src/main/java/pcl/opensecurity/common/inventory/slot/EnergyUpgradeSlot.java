package pcl.opensecurity.common.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.items.ItemEnergyUpgrade;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

import javax.annotation.Nonnull;

public class EnergyUpgradeSlot extends BaseSlot<TileEntityEnergyTurret> {

    public EnergyUpgradeSlot(TileEntityEnergyTurret tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return stack.getItem() instanceof ItemEnergyUpgrade;
    }
}
