package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.block.MagReaderBlock;
import pcl.opensecurity.data.CardData;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public final class MagReaderBlockEntity extends SecurityBlockEntity {
    private String eventName = "magData";
    private UUID owner;
    private boolean swipeIndicator = true;
    private int lightState;

    public MagReaderBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.MAG_READER_BE.get(), pos, state, "os_magreader", 32);
    }

    public boolean swipe(ItemStack stack, Player player, Direction side) {
        setVisualState(2);
        CardData card = CardData.read(stack);
        if (!card.valid() || !consumeEnergy(5)) {
            if (swipeIndicator) setVisualState(1);
            scheduleReset();
            return false;
        }
        String user = player.getName().getString();
        node.sendToReachable("computer.signal", eventName, user, card.data(), card.uuid(), card.locked(), side.get3DDataValue());
        if (level != null) level.playSound(null, worldPosition, OpenSecurity.CARD_SWIPE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        if (swipeIndicator) setVisualState(4);
        scheduleReset();
        return true;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    @Callback(direct = true, doc = "function(name:string):boolean -- Sets the swipe signal name.")
    public Object[] setEventName(Context context, Arguments args) {
        String requested = args.checkString(0).trim();
        if (requested.isEmpty()) return new Object[]{false, "event name cannot be empty"};
        eventName = requested;
        setChanged();
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function(state:number):boolean -- Sets indicator bits from 0 through 7.")
    public Object[] setLightState(Context context, Arguments args) {
        if (swipeIndicator) return new Object[]{false, "automatic swipe indicator is enabled"};
        int requested = args.checkInteger(0);
        if (requested < 0 || requested > 7) return new Object[]{false, "state must be between 0 and 7"};
        lightState = requested;
        setVisualState(requested);
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function(enabled:boolean):boolean -- Enables the automatic swipe indicator.")
    public Object[] swipeIndicator(Context context, Arguments args) {
        swipeIndicator = args.checkBoolean(0);
        if (swipeIndicator) setVisualState(0);
        else setVisualState(lightState);
        return new Object[]{true};
    }

    public void refreshVisualState() {
        setVisualState(swipeIndicator ? 0 : lightState);
    }

    private void scheduleReset() {
        if (level != null && swipeIndicator) level.scheduleTick(worldPosition, getBlockState().getBlock(), 20);
    }

    private void setVisualState(int state) {
        lightState = Math.max(0, Math.min(7, state));
        setChanged();
        if (level != null && getBlockState().hasProperty(MagReaderBlock.LIGHT)
                && getBlockState().getValue(MagReaderBlock.LIGHT) != lightState) {
            level.setBlock(worldPosition, getBlockState().setValue(MagReaderBlock.LIGHT, lightState), 3);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("eventName", eventName);
        if (owner != null) tag.putUUID("owner", owner);
        tag.putBoolean("swipeInd", swipeIndicator);
        tag.putInt("doorState", lightState);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        eventName = tag.contains("eventName") && !tag.getString("eventName").isBlank()
                ? tag.getString("eventName") : "magData";
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        swipeIndicator = !tag.contains("swipeInd") || tag.getBoolean("swipeInd");
        lightState = Math.max(0, Math.min(7, tag.getInt("doorState")));
    }
}
