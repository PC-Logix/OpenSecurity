package pcl.opensecurity.common.inventory.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;

import javax.annotation.Nonnull;

public class CardOutputSlot extends BaseSlot<TileEntityCardWriter> {

    public CardOutputSlot(TileEntityCardWriter tileEntity, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(tileEntity, itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }
}
