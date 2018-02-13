package pcl.opensecurity.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;

public class CardInputSlot extends Slot
{
    public CardInputSlot(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
    {
        super(inventoryIn, slotIndex, xPosition, yPosition);
    }

	/**
     * Check if the stack is a valid item for this slot.
     */
    public boolean isItemValid(@Nullable ItemStack stack)
    {
        return (stack.getItem() instanceof ItemCard || stack.getUnlocalizedName().equals("item.oc.EEPROM"));
    }

    public int getItemStackLimit(ItemStack stack)
    {
        return 64;
    }
}