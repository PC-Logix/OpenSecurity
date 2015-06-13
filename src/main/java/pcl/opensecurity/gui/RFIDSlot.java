package pcl.opensecurity.gui;

import pcl.opensecurity.items.ItemRFIDCard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RFIDSlot extends Slot {

	public RFIDSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	
    public boolean isItemValid(ItemStack itemstack)
    {
            if (itemstack.getItem() instanceof ItemRFIDCard) {
            	if(itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey("locked")) {
                	return true;
            	} else {
            		return false;
            	}
            }
            return false;
    }
    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
            this.onCrafting(par2ItemStack);
            super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

}