package pcl.opensecurity.common.tileentity;

import java.util.UUID;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.inventory.BasicInventory;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.items.ItemRFIDCard;

public class TileEntityCardWriter extends TileEntityMachineBase implements ITickable, ISidedInventory {

	public static final int SIZE = 2;
	private static final int[] SLOTS_TOP = new int[] {0};
	private static final int[] SLOTS_BOTTOM = new int[] {1};
	private static final int[] SLOTS_SIDES = new int[] {1};
	private final BasicInventory inv;
	protected boolean addedToNetwork = false;
	public boolean hasCards = false;

	public ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	public TileEntityCardWriter() {
		if (this.node() != null) {
			initOCFilesystem();
		}
		inv = new BasicInventory(SIZE,"Processing",64);
	}

	private String getComponentName() {
		// TODO Auto-generated method stub
		return "os_cardwriter";
	}

	private Object oc_fs;

	protected ManagedEnvironment oc_fs(){
		return (ManagedEnvironment) this.oc_fs;
	}

	private void initOCFilesystem() {
		oc_fs = li.cil.oc.api.FileSystem.asManagedEnvironment(li.cil.oc.api.FileSystem.fromClass(OpenSecurity.class, OpenSecurity.MODID, "/lua/cardwriter/"), "cardwriter");
		((Component) oc_fs().node()).setVisibility(Visibility.Network);
	}

	@Override
	public void onConnect(final Node node) {
		if(node == node()) {
			node.connect(oc_fs().node());
		}
	}

	@Override
	public void onDisconnect(final Node node) {
		if (node.host() instanceof Context) {
			node.disconnect(oc_fs().node());
		} else if (node == this.node) {
			oc_fs().node().remove();
		}
	}

	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(node != null) {
			node.remove();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(node != null) {
			node.remove();
		}
	}

	protected void addToNetwork() {
		if(!addedToNetwork) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}

	@Override
	public void update() {
		super.update();
		if(!addedToNetwork) {
			addToNetwork();
		}
		if (!hasCards && getStackInSlot(0) != null) {
			hasCards = true;
			if (node != null)
				node.sendToReachable("computer.signal", "cardInsert", "cardInsert");
		}

		if (hasCards && getStackInSlot(0) == null) {
			hasCards = false;
			if (node != null)
				node.sendToReachable("computer.signal", "cardRemove", "cardRemove");
		}
	}

	@Callback(doc = "function(string: data, string: displayName, boolean: locked, int: color):string; writes data to the card, (64 characters for RFID, or 128 for MagStripe), the rest is silently discarded, 2nd argument will change the displayed name of the card in your inventory. if you pass true to the 3rd argument you will not be able to erase, or rewrite data, the 3rd argument will set the color of the card, use OC's sides api.", direct = true)
	public Object[] write(Context context, Arguments args) {
		String data = args.checkString(0);
		String title = args.optString(1, "");
		Boolean locked = args.optBoolean(2, false);
		int colorIn = args.optInteger(3, 0);
		int color = Integer.parseInt("FFFFFF", 16);

		if (colorIn > 0 && colorIn < 15) {
			switch(colorIn) {
			case 0: color = Integer.parseInt("FFFFFF", 16); break;
			case 1: color = Integer.parseInt("FFA500", 16); break;
			case 2: color = Integer.parseInt("FF00FF", 16); break;
			case 3: color = Integer.parseInt("ADD8E6", 16); break;
			case 4: color = Integer.parseInt("FFFF00", 16); break;
			case 5: color = Integer.parseInt("00FF00", 16); break;
			case 6: color = Integer.parseInt("FFC0CB", 16); break;
			case 7: color = Integer.parseInt("808080", 16); break;
			case 8: color = Integer.parseInt("C0C0C0", 16); break;
			case 9: color = Integer.parseInt("00FFFF", 16); break;
			case 10: color = Integer.parseInt("800080", 16); break;
			case 11: color = Integer.parseInt("0000FF", 16); break;
			case 12: color = Integer.parseInt("A52A2A", 16); break;
			case 13: color = Integer.parseInt("008000", 16); break;
			case 14: color = Integer.parseInt("FF0000", 16); break;
			case 15: color = Integer.parseInt("000000", 16); break;
			default: color = Integer.parseInt("FFFFFF", 16); break;
			}
		}
		ItemStack outStack = null;
		if (node.changeBuffer(-5) == 0) {
			if (data != null) {
				if (getStackInSlot(0) != null) {
					// checking for a empty one
					if (getStackInSlot(1) == null) { // The slot is empty lets
						// make us a RFID
						if (getStackInSlot(0).getItem() instanceof ItemRFIDCard) {
							outStack = new ItemStack(ContentRegistry.itemRFIDCard);
							if (data.length() > 64) {
								data = data.substring(0, 64);
							}
						} else if (getStackInSlot(0).getItem() instanceof ItemMagCard) {
							outStack = new ItemStack(ContentRegistry.itemMagCard);
							if (data.length() > 128) {
								data = data.substring(0, 128);
							}
						}
						outStack.setTagCompound(new NBTTagCompound());
						outStack.getTagCompound().setString("data", data);
						if (!title.isEmpty()) {
							outStack.setStackDisplayName(title);
						}
						//System.out.println(CardWriterItemStacks[x].stackTagCompound.getString("uuid"));
						if (outStack.getTagCompound().getString("uuid").isEmpty()) {
							outStack.getTagCompound().setString("uuid", UUID.randomUUID().toString());
						}

						if (locked) {
							outStack.getTagCompound().setBoolean("locked", locked);
						}

						//outStack.getTagCompound().setInteger("color", color);
						NBTTagCompound nbttagcompound = outStack.getTagCompound();

						if (nbttagcompound == null)
						{
							nbttagcompound = new NBTTagCompound();
							outStack.setTagCompound(nbttagcompound);
						}

						NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

						if (!nbttagcompound.hasKey("display", 10))
						{
							nbttagcompound.setTag("display", nbttagcompound1);
						}

						nbttagcompound1.setInteger("color", color);

						decrStackSize(0, 1);

						setInventorySlotContents(1, outStack);
						return new Object[] { true,  outStack.getTagCompound().getString("uuid")};
					}
					return new Object[] { false, "No Empty Slots" };
				}
				return new Object[] { false, "No card in slot" };
			}
			return new Object[] { false, "Data is Null" };
		} else {
			return new Object[] { false, "Not enough power in OC Network." };
		}
	}

	// This item handler will hold our nine inventory slots
	private ItemStackHandler itemStackHandler = new ItemStackHandler(SIZE) {
		@Override
		protected void onContentsChanged(int slot) {
			// We need to tell the tile entity that something has changed so
			// that the chest contents is persisted
			TileEntityCardWriter.this.markDirty();
		}
	};

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		if (index == 2)
		{
			return false;
		}
		else if (index != 1)
		{
			return true;
		}
		else
		{
			//ItemStack itemstack = this.furnaceItemStacks[1];
			// return isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && (itemstack == null || itemstack.getItem() != Items.BUCKET);
		}
		return false;
	}

	public int[] getSlotsForFace(EnumFacing side)
	{
		return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : SLOTS_SIDES);
	}

	/**
	 * Returns true if automation can insert the given item in the given slot from the given side.
	 */
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
	{
		return this.isItemValidForSlot(index, itemStackIn);
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side.
	 */
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
	{
		if (direction == EnumFacing.DOWN && index == 1)
		{
			Item item = stack.getItem();

			if (item != Items.WATER_BUCKET && item != Items.BUCKET)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		inv.readFromNBT(data);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		inv.writeToNBT(data);
		return data;
	}
	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemStackHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}
}
