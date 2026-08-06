package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.data.CardData;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CardWriterBlockEntity extends SecurityBlockEntity {
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == INPUT && (stack.is(OpenSecurity.RFID_CARD.get()) || stack.is(OpenSecurity.MAG_CARD.get()));
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public CardWriterBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.CARD_WRITER_BE.get(), pos, state, "os_cardwriter", 32);
    }

    public ItemStackHandler inventory() {
        return inventory;
    }

    public boolean insert(ItemStack held) {
        if (!inventory.getStackInSlot(INPUT).isEmpty() || !inventory.isItemValid(INPUT, held)) return false;
        inventory.setStackInSlot(INPUT, held.split(1));
        node.sendToReachable("computer.signal", "cardInsert", "cardInsert");
        return true;
    }

    public boolean extract(Player player) {
        int slot = !inventory.getStackInSlot(OUTPUT).isEmpty() ? OUTPUT : INPUT;
        ItemStack stack = inventory.extractItem(slot, 1, false);
        if (stack.isEmpty()) return false;
        if (!player.addItem(stack)) player.drop(stack, false);
        if (slot == INPUT) node.sendToReachable("computer.signal", "cardRemove", "cardRemove");
        return true;
    }

    public void dropContents(Level level) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false);
            if (!stack.isEmpty()) level.addFreshEntity(new ItemEntity(level,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack));
        }
    }

    @Callback(direct = true, doc = "function(data:string[,label:string,locked:boolean,color:number]):boolean,string -- Writes the inserted card.")
    public Object[] write(Context context, Arguments args) {
        if (!consumeEnergy(5)) return new Object[]{false, "not enough energy"};
        ItemStack input = inventory.getStackInSlot(INPUT);
        if (input.isEmpty()) return new Object[]{false, "no card in input"};
        if (!inventory.getStackInSlot(OUTPUT).isEmpty()) return new Object[]{false, "output slot is not empty"};

        CardData previous = CardData.read(input);
        if (previous.locked()) return new Object[]{false, "card is locked"};
        int limit = input.is(OpenSecurity.RFID_CARD.get()) ? 64 : 128;
        String data = args.checkString(0);
        if (data.length() > limit) data = data.substring(0, limit);
        String label = args.optString(1, "");
        boolean locked = args.optBoolean(2, false);
        int colorIndex = Math.max(0, Math.min(15, args.optInteger(3, 0)));
        int color = DyeColor.byId(colorIndex).getTextureDiffuseColor();

        ItemStack output = new ItemStack(input.getItem());
        new CardData(data, previous.uuid(), locked, color).write(output);
        if (!label.isEmpty()) output.set(DataComponents.CUSTOM_NAME, Component.literal(label));
        inventory.extractItem(INPUT, 1, false);
        inventory.setStackInSlot(OUTPUT, output);
        node.sendToReachable("computer.signal", "cardRemove", "cardRemove");
        return new Object[]{true, previous.uuid()};
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inventory")) inventory.deserializeNBT(provider, tag.getCompound("inventory"));
    }
}
