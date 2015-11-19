package pcl.opensecurity.tileentity;

import java.nio.charset.Charset;
import java.util.UUID;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Component;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.common.item.EEPROM;
import li.cil.oc.server.network.Network;
import li.cil.oc.Settings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.items.ItemRFIDCard;
import scala.actors.threadpool.Arrays;

public class TileEntityCardWriter extends TileEntityMachineBase implements Environment, IInventory, ISidedInventory {

	public ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	protected boolean addedToNetwork = false;

	public TileEntityCardWriter() {
		if (this.node() != null) {
			initOCFilesystem();
		}
	}

	//private li.cil.oc.api.network.ManagedEnvironment oc_fs;

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

	private static final int[] slots_top = new int[] { 0 };
	private static final int[] slots_bottom = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private static final int[] slots_sides = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	private ItemStack[] CardWriterItemStacks = new ItemStack[12];

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
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
		return this.CardWriterItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.CardWriterItemStacks[i];
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
		if (getStackInSlot(i) != null) {
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i, null);
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.CardWriterItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return "os_cardwriter";
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
			if (itemstack.getItem() instanceof ItemRFIDCard || itemstack.getItem() instanceof ItemMagCard || itemstack.getItem() instanceof EEPROM) {
				if (itemstack.stackTagCompound == null || !itemstack.stackTagCompound.hasKey("locked") && !itemstack.stackTagCompound.hasKey("oc:readonly")) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public String getComponentName() {
		return "os_cardwriter";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if(!addedToNetwork) {
			addToNetwork();
		}
		//if (node != null && node.network() == null) {
		//	Network.joinOrCreateNetwork(this);
		//}
	}

	protected void addToNetwork() {
		if(!addedToNetwork) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		if (oc_fs != null && oc_fs().node() != null) {
			oc_fs().node().load(nbt.getCompoundTag("oc:fs"));
		}

		NBTTagList var2 = nbt.getTagList("Items", nbt.getId());
		this.CardWriterItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.CardWriterItemStacks.length) {
				this.CardWriterItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		if (oc_fs != null && oc_fs().node() != null) {
			final NBTTagCompound fsNbt = new NBTTagCompound();
			oc_fs().node().save(fsNbt);
			nbt.setTag("oc:fs", fsNbt);
		}

		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.CardWriterItemStacks.length; ++var3) {
			if (this.CardWriterItemStacks[var3] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) var3);
				this.CardWriterItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		nbt.setTag("Items", var2);
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

	@Callback
	public Object[] flash(Context context, Arguments args) {
		byte[] code = args.checkString(0).getBytes(Charset.forName("UTF-8"));
		String title = args.checkString(1);
		Boolean locked = args.checkBoolean(2);
		ItemStack eepromItem = li.cil.oc.api.Items.get("eeprom").createItemStack(1);
		if (code != null) {
			if (getStackInSlot(0) != null) {
				for (int x = 3; x <= 12; x++) { // Loop the 9 output slots
					// checking for a empty one
					if (getStackInSlot(x) == null) { // The slot is empty lets
						// make us a RFID
						if (getStackInSlot(0).getItem() instanceof EEPROM) {
							CardWriterItemStacks[x] = eepromItem;
							NBTTagCompound oc_data = new NBTTagCompound();
							NBTTagCompound our_data = new NBTTagCompound();
							if(code.length > Settings.get().eepromSize()) {
								code = Arrays.copyOfRange(code, 0, 4096);
							}
							if(title.length() > Settings.get().eepromDataSize()) {
								title = title.substring(0, Settings.get().eepromDataSize());
							}
							our_data.setByteArray("oc:eeprom", code);
							our_data.setString("oc:label", title);
							our_data.setBoolean("oc:readonly", locked);
							oc_data.setTag("oc:data", our_data);
							CardWriterItemStacks[x].setTagCompound(oc_data);
							decrStackSize(0, 1);
							return new Object[] { true };
						}
						return new Object[] { false, "Item is not EEPROM" };
					}
				}
				return new Object[] { false, "No Empty Slots" };
			}
			return new Object[] { false, "No EEPROM in slot" };
		}
		return new Object[] { false, "Data is Null" };
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

		if (node.changeBuffer(-5) == 0) {
			if (data != null) {
				if (getStackInSlot(0) != null) {
					for (int x = 3; x <= 12; x++) { // Loop the 9 output slots
						// checking for a empty one
						if (getStackInSlot(x) == null) { // The slot is empty lets
							// make us a RFID
							if (getStackInSlot(0).getItem() instanceof ItemRFIDCard) {
								CardWriterItemStacks[x] = new ItemStack(ContentRegistry.rfidCard);
								if (data.length() > 64) {
									data = data.substring(0, 64);
								}
							} else if (getStackInSlot(0).getItem() instanceof ItemMagCard) {
								CardWriterItemStacks[x] = new ItemStack(ContentRegistry.magCard);
								if (data.length() > 128) {
									data = data.substring(0, 128);
								}
							}
							CardWriterItemStacks[x].setTagCompound(new NBTTagCompound());
							CardWriterItemStacks[x].stackTagCompound.setString("data", data);
							if (!title.isEmpty()) {
								CardWriterItemStacks[x].setStackDisplayName(title);
							}
							System.out.println(CardWriterItemStacks[x].stackTagCompound.getString("uuid"));
							if (CardWriterItemStacks[x].stackTagCompound.getString("uuid").isEmpty()) {
								CardWriterItemStacks[x].stackTagCompound.setString("uuid", UUID.randomUUID().toString());
							}

							if (locked) {
								CardWriterItemStacks[x].stackTagCompound.setBoolean("locked", locked);
							}

							CardWriterItemStacks[x].stackTagCompound.setInteger("color", color);

							decrStackSize(0, 1);
							return new Object[] { true,  CardWriterItemStacks[x].stackTagCompound.getString("uuid")};
						}
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

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}
}
