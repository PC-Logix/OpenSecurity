package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.Config;
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

public final class SecureDoorBlockEntity extends SecurityBlockEntity {
    private UUID owner;
    private String password = "";
    private String eventName = "magData";

    public SecureDoorBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.SECURE_DOOR_BE.get(), pos, state, "os_securedoor", 32);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public boolean canModify(UUID player) {
        return owner == null || owner.equals(player);
    }

    public String password() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setChanged();
    }

    public boolean swipe(ItemStack stack, Player player, Direction side) {
        CardData card = CardData.read(stack);
        if (!card.valid() || !consumeEnergy(5)) return false;
        node.sendToReachable("computer.signal", eventName, player.getName().getString(),
                card.data(), Config.exposedUuid(card.uuid()), card.locked(), side.get3DDataValue());
        if (level != null) level.playSound(null, worldPosition, OpenSecurity.CARD_SWIPE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Callback(direct = true, doc = "function(name:string):boolean -- Sets the built-in reader signal name.")
    public Object[] setEventName(Context context, Arguments args) {
        String requested = args.checkString(0).trim();
        if (requested.isEmpty()) return new Object[]{false, "event name cannot be empty"};
        eventName = requested;
        setChanged();
        return new Object[]{true};
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null) tag.putUUID("owner", owner);
        tag.putString("password", password);
        tag.putString("eventName", eventName);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        password = tag.getString("password");
        eventName = tag.contains("eventName") && !tag.getString("eventName").isBlank()
                ? tag.getString("eventName") : "magData";
    }
}
