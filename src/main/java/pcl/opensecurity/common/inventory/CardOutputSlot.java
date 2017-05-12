package pcl.opensecurity.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.items.ItemCard;

public class CardOutputSlot extends Slot
{
    public CardOutputSlot(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
    {
        super(inventoryIn, slotIndex, xPosition, yPosition);
    }

    /**
     * Check if the stack is a valid item for this slot.
     */
    public boolean isItemValid(@Nullable ItemStack stack)
    {
        return false;
    }

    public int getItemStackLimit(ItemStack stack)
    {
        return 1;
    }
}