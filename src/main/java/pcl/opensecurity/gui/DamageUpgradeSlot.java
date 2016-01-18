package pcl.opensecurity.gui;

import pcl.opensecurity.items.ItemDamageUpgrade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class DamageUpgradeSlot extends Slot {

	public DamageUpgradeSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	@Override
    public boolean isItemValid(ItemStack itemstack)
    {

            if (itemstack.getItem() instanceof ItemDamageUpgrade) {
            	return true;
            }
            return false;
    }
	@Override
    public int getSlotStackLimit()
    {
        return 1;
    }
    
    /**
     * Called when the player picks up an item from an inventory slot
     */
	@Override
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
            this.onCrafting(par2ItemStack);
            super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }

}