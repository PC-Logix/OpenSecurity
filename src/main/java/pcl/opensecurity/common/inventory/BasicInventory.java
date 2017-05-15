package pcl.opensecurity.common.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

import java.util.LinkedList;

public class BasicInventory implements IInventory
{
	private final ItemStack[] contents;
	private final String name;
	private final int stackLimit;
	private final LinkedList<TileEntity> listeners = new LinkedList<TileEntity>();

	public BasicInventory(int slots, String name, int stackLimit) {
		this.contents = new ItemStack[slots];
		this.name = name;
		this.stackLimit = stackLimit;
	}

	@Override
	public int getSizeInventory() {
		return contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return contents[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int num) {
		if (slot < contents.length && contents[slot] != null) {
			if (contents[slot].stackSize > num) {
				ItemStack output = contents[slot].splitStack(num);
				markDirty();
				return output;
			}
			ItemStack output = contents[slot];
			setInventorySlotContents(slot, null);
			return output;
		}
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if (this.contents[slot] == null) {
			return null;
		}
		ItemStack pullStack = this.contents[slot];
		setInventorySlotContents(slot, null);
		return pullStack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack newStack) {
		if (slot >= contents.length) {
			return;
		}
		contents[slot] = newStack;

		if (newStack != null && newStack.stackSize > this.getInventoryStackLimit()) {
			newStack.stackSize = this.getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getInventoryStackLimit() {
		return this.stackLimit;
	}

	@Override
	public void markDirty() {
		for (TileEntity handler : this.listeners) {
			handler.markDirty();
		}
	}



	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	public void writeToNBT(NBTTagCompound tagCompound){
		NBTTagList slots = new NBTTagList();
		for (byte index = 0; index < this.contents.length; ++index) {
			if (this.contents[index] != null && this.contents[index].stackSize > 0) {
				NBTTagCompound slot = new NBTTagCompound();
				slots.appendTag(slot);
				slot.setByte("Slot",index);
				this.contents[index].writeToNBT(slot);
			}
		}
		tagCompound.setTag("items", slots);
	}

	public void readFromNBT(NBTTagCompound data) {
		NBTTagList slots = data.getTagList("items", Constants.NBT.TAG_COMPOUND);

		for (int j = 0; j < slots.tagCount(); ++j) {
			NBTTagCompound slot = slots.getCompoundTagAt(j);
			int index;
			if (slot.hasKey("index")) {
				index = slot.getInteger("index");
			} else {
				index = slot.getByte("Slot");
			}
			if (index >= 0 && index < this.contents.length) {
				setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
			}
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}
}