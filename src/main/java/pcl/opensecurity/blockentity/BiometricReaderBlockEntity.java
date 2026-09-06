package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public final class BiometricReaderBlockEntity extends SecurityBlockEntity {
    private String eventName = "bioReader";

    public BiometricReaderBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.BIOMETRIC_READER_BE.get(), pos, state, "os_biometric", 32);
    }

    public void read(Player player) {
        node.sendToReachable("computer.signal", eventName, player.getUUID().toString(), player.getName().getString());
    }

    @Callback(doc = "function(name:string):boolean -- Sets the emitted signal name.")
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
        tag.putString("eventName", eventName);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        eventName = tag.contains("eventName") && !tag.getString("eventName").isBlank()
                ? tag.getString("eventName") : "bioReader";
    }
}
