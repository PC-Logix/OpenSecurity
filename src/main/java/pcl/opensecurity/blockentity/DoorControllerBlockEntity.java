package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.block.SecureDoorBlock;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class DoorControllerBlockEntity extends SecurityBlockEntity {
    private static final String CAMOUFLAGE_TAG = "camouflage";
    private UUID owner;
    private BlockState camouflage;

    public DoorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.DOOR_CONTROLLER_BE.get(), pos, state, "os_doorcontroller", 32);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
    }

    public boolean canModify(UUID player) {
        return owner == null || owner.equals(player);
    }

    public BlockState getCamouflage() {
        return camouflage;
    }

    public void setCamouflage(BlockState state) {
        if (state.isAir() || state.is(OpenSecurity.DOOR_CONTROLLER.get())) {
            clearCamouflage();
            return;
        }
        camouflage = state;
        syncCamouflage();
    }

    public void clearCamouflage() {
        camouflage = null;
        syncCamouflage();
    }

    private void syncCamouflage() {
        setChanged();
        requestModelDataUpdate();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ModelData getModelData() {
        return camouflage == null ? ModelData.EMPTY : ModelData.of(RollDoorBlockEntity.CAMOUFLAGE, camouflage);
    }

    @Callback(direct = true, doc = "function():table -- Returns the open state of adjacent secure doors.")
    public Object[] isOpen(Context context, Arguments args) {
        List<Boolean> states = new ArrayList<>();
        for (BlockPos doorPos : doors()) states.add(level.getBlockState(doorPos).getValue(DoorBlock.OPEN));
        return new Object[]{states.toArray()};
    }

    @Callback(doc = "function([password:string]):table -- Toggles adjacent secure doors.")
    public Object[] toggle(Context context, Arguments args) {
        Set<BlockPos> doors = doors();
        if (doors.isEmpty()) return new Object[]{false, "no secure door found"};
        boolean open = !level.getBlockState(doors.iterator().next()).getValue(DoorBlock.OPEN);
        return setDoorStates(open, args.optString(0, ""));
    }

    @Callback(doc = "function([password:string]):table -- Opens adjacent secure doors.")
    public Object[] open(Context context, Arguments args) {
        return setDoorStates(true, args.optString(0, ""));
    }

    @Callback(doc = "function([password:string]):table -- Closes adjacent secure doors.")
    public Object[] close(Context context, Arguments args) {
        return setDoorStates(false, args.optString(0, ""));
    }

    @Callback(doc = "function([oldPassword:string],newPassword:string):table -- Sets or changes adjacent door passwords.")
    public Object[] setPassword(Context context, Arguments args) {
        if (args.count() == 1) {
            return setDoorPasswords("", args.checkString(0));
        }

        if (args.count() >= 2) {
            return setDoorPasswords(args.checkString(0), args.checkString(1));
        }

        return new Object[]{false, "missing arguments"};
    }

    @Callback(doc = "function(password:string):table -- Removes adjacent door passwords.")
    public Object[] removePassword(Context context, Arguments args) {
        return setDoorPasswords(args.checkString(0), "");
    }

    private Object[] setDoorStates(boolean open, String password) {
        if (level == null) return new Object[]{false, "world is unavailable"};
        List<Object[]> responses = new ArrayList<>();
        for (BlockPos doorPos : doors()) {
            if (!(level.getBlockEntity(doorPos) instanceof SecureDoorBlockEntity door)) continue;
            if (!door.password().equals(password)) {
                responses.add(new Object[]{false, "password incorrect"});
                continue;
            }
            BlockState state = level.getBlockState(doorPos);
            ((SecureDoorBlock) state.getBlock()).setOpen(null, level, state, doorPos, open);
            responses.add(new Object[]{true});
        }
        return responses.isEmpty() ? new Object[]{false, "no secure door found"} : new Object[]{responses.toArray()};
    }

    private Object[] setDoorPasswords(String oldPassword, String newPassword) {
        if (level == null) return new Object[]{false, "world is unavailable"};
        List<Object[]> responses = new ArrayList<>();
        for (BlockPos doorPos : doors()) {
            if (!(level.getBlockEntity(doorPos) instanceof SecureDoorBlockEntity door)) continue;
            if (!door.password().isEmpty() && !door.password().equals(oldPassword)) {
                responses.add(new Object[]{false, "password was not changed"});
                continue;
            }
            door.setPassword(newPassword);
            responses.add(new Object[]{true, oldPassword.isEmpty() ? "password set" : "password changed"});
        }
        return responses.isEmpty() ? new Object[]{false, "no secure door found"} : new Object[]{responses.toArray()};
    }

    private Set<BlockPos> doors() {
        Set<BlockPos> result = new LinkedHashSet<>();
        if (level == null) return result;
        for (Direction direction : Direction.values()) addDoor(result, worldPosition.relative(direction));
        List<BlockPos> primary = List.copyOf(result);
        for (BlockPos door : primary) {
            for (Direction direction : Direction.Plane.HORIZONTAL) addDoor(result, door.relative(direction));
        }
        return result;
    }

    private void addDoor(Set<BlockPos> result, BlockPos candidate) {
        BlockState state = level.getBlockState(candidate);
        if (state.getBlock() instanceof SecureDoorBlock) result.add(SecureDoorBlock.lowerPos(candidate, state));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (owner != null) tag.putUUID("owner", owner);
        if (camouflage != null) tag.put(CAMOUFLAGE_TAG, NbtUtils.writeBlockState(camouflage));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        camouflage = null;
        if (tag.contains(CAMOUFLAGE_TAG, Tag.TAG_COMPOUND)) {
            try {
                BlockState state = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound(CAMOUFLAGE_TAG));
                if (!state.isAir() && !state.is(OpenSecurity.DOOR_CONTROLLER.get())) camouflage = state;
            } catch (RuntimeException ignored) {
                // Ignore invalid or removed camouflage blocks and use the normal controller model.
            }
        }
        requestModelDataUpdate();
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        CompoundTag tag = packet.getTag();
        if (!tag.isEmpty()) loadWithComponents(tag, provider);
        requestModelDataUpdate();
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
}
