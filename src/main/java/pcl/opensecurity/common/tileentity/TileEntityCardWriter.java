package pcl.opensecurity.common.tileentity;

import li.cil.oc.Settings;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.items.ItemRFIDCard;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

public class TileEntityCardWriter extends TileEntityOSBase implements ITickable {
    public static final int SIZE = 2;
    public boolean hasCards = false;

    private ItemStackHandler inventoryInput;
    private ItemStackHandler inventoryOutput;

    public TileEntityCardWriter() {
        super("os_cardwriter");
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
        if (this.node() != null) {
            initOCFilesystem("/lua/cardwriter/", "cardwriter");
        }
        inventoryInput = new ItemStackHandler(1);
        inventoryOutput = new ItemStackHandler(1) {
            @Override
            protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
                return 1;
            }
        };
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
    public void update() {
        super.update();
        if (!hasCards && !inventoryInput.getStackInSlot(0).isEmpty()) {
            hasCards = true;
            if (node != null)
                node.sendToReachable("computer.signal", "cardInsert", "cardInsert");
        }

        if (hasCards && inventoryInput.getStackInSlot(0).isEmpty()) {
            hasCards = false;
            if (node != null)
                node.sendToReachable("computer.signal", "cardRemove", "cardRemove");
        }
    }


    @Callback
    public Object[] flash(Context context, Arguments args) {
        byte[] code = args.checkString(0).getBytes(Charset.forName("UTF-8"));
        String title = args.checkString(1);
        Boolean locked = args.checkBoolean(2);
        ItemStack eepromItem = li.cil.oc.api.Items.get("eeprom").createItemStack(1);
        ItemStack outStack;
        if (!inventoryInput.getStackInSlot(0).isEmpty()) {
            // checking for a empty one
            if (inventoryOutput.getStackInSlot(0).isEmpty()) { // The slot is empty lets
                // make us a new EEPROM
                System.out.println(inventoryInput.getStackInSlot(0).getItem().getUnlocalizedName());
                if (inventoryInput.getStackInSlot(0).getUnlocalizedName().equals("item.oc.EEPROM")) {
                    //CardWriterItemStacks[x] = eepromItem;
                    boolean biggerEEPROM = Config.getConfig().getCategory("general").get("biggerEEPROM").getBoolean();
                    outStack = eepromItem;
                    NBTTagCompound oc_data = new NBTTagCompound();
                    NBTTagCompound our_data = new NBTTagCompound();
                    Integer biggerSizeCode = Settings.get().eepromSize()*2;
                    Integer biggerSizeData = Settings.get().eepromDataSize()*2;
                    if(!biggerEEPROM && code.length > Settings.get().eepromSize()) {
                        code = Arrays.copyOfRange(code, 0, Settings.get().eepromSize());
                    } else if(biggerEEPROM && code.length > biggerSizeCode) {
                        code = Arrays.copyOfRange(code, 0, biggerSizeCode);
                    }
                    if(!biggerEEPROM && title.length() > Settings.get().eepromDataSize()) {
                        title = title.substring(0, Settings.get().eepromDataSize());
                    } else if(biggerEEPROM && title.length() > biggerSizeData) {
                        title = title.substring(0, biggerSizeData);
                    }
                    our_data.setByteArray("oc:eeprom", code);
                    our_data.setString("oc:label", title);
                    our_data.setBoolean("oc:readonly", locked);
                    oc_data.setTag("oc:data", our_data);
                    outStack.setTagCompound(oc_data);
                    inventoryOutput.setStackInSlot(0, outStack);
                    inventoryInput.getStackInSlot(0).setCount(inventoryInput.getStackInSlot(0).getCount() - 1);
                    return new Object[] { true };
                }
                return new Object[] { false, "Item is not EEPROM" };
            }
            return new Object[] { false, "No Empty Slots" };
        }
        return new Object[] { false, "No EEPROM in slot" };
    }


    @Callback(doc = "function(string: data, string: displayName, boolean: locked, int: color):string; writes data to the card, (64 characters for RFID, or 128 for MagStripe), the rest is silently discarded, 2nd argument will change the displayed name of the card in your inventory. if you pass true to the 3rd argument you will not be able to erase, or rewrite data, the 3rd argument will set the color of the card, use OC's color api.", direct = true)
    public Object[] write(Context context, Arguments args) {
        String data = args.checkString(0);
        String title = args.optString(1, "");
        Boolean locked = args.optBoolean(2, false);
        int colorIn = args.optInteger(3, 0);
        int color = Integer.parseInt("FFFFFF", 16);

        if (colorIn >= 0 && colorIn <= 15) {
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
        ItemStack outStack;
        if (node.changeBuffer(-5) == 0) {
            if (data != null) {
                if (!inventoryInput.getStackInSlot(0).isEmpty()) {
                    // checking for a empty one
                    if (inventoryOutput.getStackInSlot(0).isEmpty()) { // The slot is empty lets
                        // make us a RFID
                        if (inventoryInput.getStackInSlot(0).getItem() instanceof ItemRFIDCard) {
                            outStack = new ItemStack(ContentRegistry.itemRFIDCard);
                            if (data.length() > 64) {
                                data = data.substring(0, 64);
                            }
                        } else if (inventoryInput.getStackInSlot(0).getItem() instanceof ItemMagCard) {
                            outStack = new ItemStack(ContentRegistry.itemMagCard);
                            if (data.length() > 128) {
                                data = data.substring(0, 128);
                            }
                        } else
                             return new Object[] { false, "Wrong item in input slot" };

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

                        inventoryInput.getStackInSlot(0).setCount(inventoryInput.getStackInSlot(0).getCount() - 1);
                        inventoryOutput.setStackInSlot(0, outStack);

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

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventoryInput.deserializeNBT(data.getCompoundTag("invIn"));
        inventoryOutput.deserializeNBT(data.getCompoundTag("invOut"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("invIn", inventoryInput.serializeNBT());
        data.setTag("invOut", inventoryOutput.serializeNBT());
        return data;
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != EnumFacing.UP) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN)
                return (T) inventoryOutput;
            else if (facing != EnumFacing.UP)
                return (T) inventoryInput;
        }

        return super.getCapability(capability, facing);
    }
}
