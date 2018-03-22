package pcl.opensecurity.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

public class EnergyTurretContainer  extends Container {
	
	protected TileEntityEnergyTurret tileEntity;

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
		return tileEntity.isUsableByPlayer(player);
	}

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < TileEntityCardWriter.SIZE) {
                if (!this.mergeItemStack(itemstack1, TileEntityCardWriter.SIZE, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, TileEntityCardWriter.SIZE, false)) {
                return null;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

}
