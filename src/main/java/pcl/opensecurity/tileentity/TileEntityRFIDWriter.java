package pcl.opensecurity.tileentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemRFIDCard;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityRFIDWriter extends TileEntityMachineBase implements SimpleComponent, IInventory, ISidedInventory  {

	public TileEntityRFIDWriter() { }

	private static final int[] slots_top = new int[] {2};
	private static final int[] slots_bottom = new int[] {3,4,5,6,7,8,9};
	private static final int[] slots_sides = new int[] {0,1};
	private ItemStack[] RFIDWriterItemStacks = new ItemStack[20];
	
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
				player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return this.isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return this.RFIDWriterItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.RFIDWriterItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (getStackInSlot(i) != null)
		{
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i,null);
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.RFIDWriterItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "OSRFIDWriter";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0) {
			if (itemstack.getItem() instanceof ItemRFIDCard) {
				if (itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey("locked")) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	public String getComponentName() {
		return "OSRFIDWriter";
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items",par1NBTTagCompound.getId());
		this.RFIDWriterItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.RFIDWriterItemStacks.length)
			{
				this.RFIDWriterItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.RFIDWriterItemStacks.length; ++var3)
		{
			if (this.RFIDWriterItemStacks[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.RFIDWriterItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		par1NBTTagCompound.setTag("Items", var2);
	}
	
	@Override
	public net.minecraft.network.Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}
	
	@Callback(doc = "function(string: data, boolean: locked):string; writes data to the RFID, upto 64 characters, the rest is silently discarded, if you pass true to the 2nd argument you will not be able to erase, or rewrite data.", direct = true)
	public Object[] write(Context context, Arguments args) {
		String data = args.checkString(0);
		Boolean locked = args.optBoolean(1, false);
		if (data != null) {
			if (getStackInSlot(0) != null) {
					for (int x = 3; x <= 12; x++) { //Loop the 9 output slots checking for a empty one
						if (getStackInSlot(x) == null) { //The slot is empty lets make us a NameTag

							RFIDWriterItemStacks[x] = new ItemStack(OpenSecurity.rfidCard);
							RFIDWriterItemStacks[x].setTagCompound(new NBTTagCompound());
							if (data.length() > 64) {
								data = data.substring(0, 64);
							}
							System.out.println(data.length());
							RFIDWriterItemStacks[x].stackTagCompound.setString("data", data);

							if(locked) {
								RFIDWriterItemStacks[x].stackTagCompound.setBoolean("locked", locked);
							}
							decrStackSize(0, 1);
							return new Object[]{true};
						}
					} return new Object[]{false + " No Empty Slots"};
			} return new Object[]{false + " No RFID in slot"};
		}  return new Object[]{false + " Data is Null"};
	}
	
}
