package pcl.opensecurity.gui;

import li.cil.oc.common.item.EEPROM;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;

public class CardSlot extends Slot {

	public CardSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		if (itemstack.getItem() instanceof ItemRFIDCard || itemstack.getItem() instanceof ItemMagCard || itemstack.getItem() instanceof EEPROM) {
			return itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey("locked") && !itemstack.stackTagCompound.hasKey("oc:readonly");
		}
		return false;
	}
	/**
	 * Called when the player picks up an item from an inventory slot
	 * 
	 * public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack
	 * par2ItemStack) { this.onCrafting(par2ItemStack);
	 * super.onPickupFromSlot(par1EntityPlayer, par2ItemStack); }
	 */

}