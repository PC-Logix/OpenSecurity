package pcl.opensecurity.containers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.gui.CooldownUpgradeSlot;
import pcl.opensecurity.gui.DamageUpgradeSlot;
import pcl.opensecurity.gui.EnergyUpgradeSlot;
import pcl.opensecurity.gui.MovementUpgradeSlot;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;

public class EnergyTurretContainer extends Container {
	protected TileEntityEnergyTurret tileEntity;
	private static final int
	INV_START = 8, // start at next slot after armor, e.g. 14
	INV_END = INV_START+26, // 27 vanilla inventory slots total (i.e. the first one plus 26 more)
	HOTBAR_START = INV_END+1, // start at next slot after inventory
	HOTBAR_END = HOTBAR_START+8; // 9 slots total (i.e. the first one plus 8 more)

	public EnergyTurretContainer(InventoryPlayer inventory, TileEntityEnergyTurret te) {
		tileEntity = te;
		int i = 0;
	
		addSlotToContainer(new DamageUpgradeSlot(tileEntity, 0, 8, 13));
		addSlotToContainer(new DamageUpgradeSlot(tileEntity, 1, 8, 31));
		addSlotToContainer(new MovementUpgradeSlot(tileEntity, 2, 8, 49));
		addSlotToContainer(new MovementUpgradeSlot(tileEntity, 3, 8, 67));
		
		addSlotToContainer(new CooldownUpgradeSlot(tileEntity, 4, 152, 13));
		addSlotToContainer(new CooldownUpgradeSlot(tileEntity, 5, 152, 31));
		addSlotToContainer(new EnergyUpgradeSlot(tileEntity, 6, 152, 49));
		addSlotToContainer(new EnergyUpgradeSlot(tileEntity, 7, 152, 67));


		// PLAYER INVENTORY - uses default locations for standard inventory texture file
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 89 + i * 18));
			}
		}

		// PLAYER ACTION BAR - uses default locations for standard action bar texture file
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 147));
		}

	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			
			// If item is in our custom Inventory or an ARMOR slot
			if (index < INV_START) {
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true)) {
					return null;
				}
				slot.onSlotChange(itemstack1, itemstack);
			}
			
			// Item is in inventory / hotbar, try to place in custom inventory or armor slots
			else {
				if (index >= INV_START && !this.mergeItemStack(itemstack1, 0, 8, false)) {
					// place in custom inventory
					return null;
				}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
		// this will prevent the player from interacting with the item that opened the inventory:
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
			return null;
		}
		return super.slotClick(slot, button, flag, player);
	}

	// IMPORTANT to override the mergeItemStack method if your inventory stack size limit is 1
	/**
	 * Vanilla method fails to account for stack size limits of one, resulting in only one
	 * item getting placed in the slot and the rest disappearing into thin air; vanilla
	 * method also fails to check whether stack is valid for slot
	 */
	@Override
	protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards)
	{
		boolean flag1 = false;
		int k = backwards ? end - 1 : start;
		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable())
		{
			while (stack.stackSize > 0 && (!backwards && k < end || backwards && k >= start))
			{
				slot = (Slot) inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += (backwards ? -1 : 1);
					continue;
				}

				if (itemstack1 != null && itemstack1.getItem() == stack.getItem() &&
						(!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) &&
						ItemStack.areItemStackTagsEqual(stack, itemstack1))
				{
					int l = itemstack1.stackSize + stack.stackSize;

					if (l <= stack.getMaxStackSize() && l <= slot.getSlotStackLimit()) {
						stack.stackSize = 0;
						itemstack1.stackSize = l;
						//TileEntityEnergyTurret.markDirty();
						flag1 = true;
					} else if (itemstack1.stackSize < stack.getMaxStackSize() && l < slot.getSlotStackLimit()) {
						stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = stack.getMaxStackSize();
						//TileEntityEnergyTurret.markDirty();
						flag1 = true;
					}
				}

				k += (backwards ? -1 : 1);
			}
		}

		if (stack.stackSize > 0)
		{
			k = backwards ? end - 1 : start;

			while (!backwards && k < end || backwards && k >= start) {
				slot = (Slot) inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (!slot.isItemValid(stack)) {
					k += (backwards ? -1 : 1);
					continue;
				}

				if (itemstack1 == null) {
					int l = stack.stackSize;

					if (l <= slot.getSlotStackLimit()) {
						slot.putStack(stack.copy());
						stack.stackSize = 0;
						//inventory.markDirty();
						flag1 = true;
						break;
					} else {
						putStackInSlot(k, new ItemStack(stack.getItem(), slot.getSlotStackLimit(), stack.getItemDamage()));
						stack.stackSize -= slot.getSlotStackLimit();
						//inventory.markDirty();
						flag1 = true;
					}
				}

				k += (backwards ? -1 : 1);
			}
		}

		return flag1;
	}
}