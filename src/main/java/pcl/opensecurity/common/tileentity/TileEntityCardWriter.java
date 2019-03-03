package pcl.opensecurity.common.tileentity;

import li.cil.oc.Settings;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.opensecurity.Config;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.items.ItemRFIDCard;

import javax.annotation.Nonnull;
import java.awt.*;
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
        if (node() != null) {
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
        if(node.equals(node())) {
            node.connect(oc_fs().node());
        }
    }

    @Override
    public void onDisconnect(final Node node) {
        if (node.host() instanceof Context) {
            node.disconnect(oc_fs().node());
        } else if (node.equals(node())) {
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
        boolean locked = args.checkBoolean(2);
        ItemStack eepromItem = li.cil.oc.api.Items.get("eeprom").createItemStack(1);
        ItemStack outStack;
        if (!inventoryInput.getStackInSlot(0).isEmpty()) {
            // checking for a empty one
            if (inventoryOutput.getStackInSlot(0).isEmpty()) { // The slot is empty lets
                // make us a new EEPROM
                System.out.println(inventoryInput.getStackInSlot(0).getItem().getUnlocalizedName());
                if (inventoryInput.getStackInSlot(0).getItem().equals(li.cil.oc.api.Items.get("eeprom").item())) {
                    //CardWriterItemStacks[x] = eepromItem;
                    boolean biggerEEPROM = Config.getConfig().getCategory("general").get("biggerEEPROM").getBoolean();
                    outStack = eepromItem;
                    NBTTagCompound oc_data = new NBTTagCompound();
                    NBTTagCompound our_data = new NBTTagCompound();
                    int biggerSizeCode = Settings.get().eepromSize()*2;
                    int biggerSizeData = Settings.get().eepromDataSize()*2;
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

        if (data == null)
            return new Object[] { false, "Data is Null" };

        if (node.changeBuffer(-5) != 0)
            return new Object[] { false, "Not enough power in OC Network." };

        if (inventoryInput.getStackInSlot(0).isEmpty())
            return new Object[] { false, "No card in slot" };

        if (!inventoryOutput.getStackInSlot(0).isEmpty())
            return new Object[] { false, "No Empty Slots" };

        String title = args.optString(1, "");
        boolean locked = args.optBoolean(2, false);
        int colorIndex = Math.max(0, Math.min(args.optInteger(3, 0), 15));

        float dyeColor[] = EnumDyeColor.byMetadata(colorIndex).getColorComponentValues();
        int color = new Color(dyeColor[0], dyeColor[1], dyeColor[2]).getRGB();

        ItemStack outStack;

        if (inventoryInput.getStackInSlot(0).getItem() instanceof ItemRFIDCard) {
            outStack = new ItemStack(ItemRFIDCard.DEFAULTSTACK.getItem());
            if (data.length() > 64) {
                data = data.substring(0, 64);
            }
        } else if (inventoryInput.getStackInSlot(0).getItem() instanceof ItemMagCard) {
            outStack = new ItemStack(ItemMagCard.DEFAULTSTACK.getItem());
            if (data.length() > 128) {
                data = data.substring(0, 128);
            }
        } else
            return new Object[] { false, "Wrong item in input slot" };

        ItemCard.CardTag cardTag = new ItemCard.CardTag(inventoryInput.getStackInSlot(0));
        cardTag.color = color;
        cardTag.dataTag = data;
        cardTag.locked = locked;

        outStack.setTagCompound(cardTag.writeToNBT(new NBTTagCompound()));

        if (!title.isEmpty()) {
            outStack.setStackDisplayName(title);
        }

        inventoryInput.getStackInSlot(0).setCount(inventoryInput.getStackInSlot(0).getCount() - 1);
        inventoryOutput.setStackInSlot(0, outStack);

        return new Object[] { true,  outStack.getTagCompound().getString("uuid")};
    }

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
        return capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
            if(facing.equals(EnumFacing.DOWN))
                return (T) inventoryOutput;
            else
                return (T) inventoryInput;
        }

        return super.getCapability(capability, facing);
    }
}
