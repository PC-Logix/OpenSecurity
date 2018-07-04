package pcl.opensecurity.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import pcl.opensecurity.common.inventory.slot.CooldownUpgradeSlot;
import pcl.opensecurity.common.inventory.slot.DamageUpgradeSlot;
import pcl.opensecurity.common.inventory.slot.EnergyUpgradeSlot;
import pcl.opensecurity.common.inventory.slot.MovementUpgradeSlot;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

import javax.annotation.Nonnull;

public class EnergyTurretContainer extends Container {

    protected TileEntityEnergyTurret tileEntity;

    public EnergyTurretContainer(InventoryPlayer inventory, TileEntityEnergyTurret te) {
        tileEntity = te;

        IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new DamageUpgradeSlot(tileEntity, itemHandler, 0, 8, 13));
        addSlotToContainer(new DamageUpgradeSlot(tileEntity, itemHandler, 1, 8, 31));
        addSlotToContainer(new MovementUpgradeSlot(tileEntity, itemHandler, 2, 8, 49));
        addSlotToContainer(new MovementUpgradeSlot(tileEntity, itemHandler, 3, 8, 67));

        addSlotToContainer(new CooldownUpgradeSlot(tileEntity, itemHandler, 4, 152, 13));
        addSlotToContainer(new CooldownUpgradeSlot(tileEntity, itemHandler, 5, 152, 31));
        addSlotToContainer(new EnergyUpgradeSlot(tileEntity, itemHandler, 6, 152, 49));
        addSlotToContainer(new EnergyUpgradeSlot(tileEntity, itemHandler, 7, 152, 67));


        // PLAYER INVENTORY - uses default locations for standard inventory texture file
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 89 + i * 18));
            }
        }

        // PLAYER ACTION BAR - uses default locations for standard action bar texture file
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 147));
        }

    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < TileEntityCardWriter.SIZE) {
                if (!this.mergeItemStack(itemstack1, TileEntityCardWriter.SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, TileEntityCardWriter.SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0)
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }

        return itemstack != null ? itemstack : super.transferStackInSlot(playerIn, index);
    }

}
