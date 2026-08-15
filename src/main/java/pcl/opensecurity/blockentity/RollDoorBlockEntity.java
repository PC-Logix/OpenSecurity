package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public final class RollDoorBlockEntity extends BlockEntity {
    public static final ModelProperty<BlockState> CAMOUFLAGE = new ModelProperty<>();
    private static final int MAX_HEIGHT = 15;
    private static final String COLOR_TAG = "rollDoorColor";
    private static final String CAMOUFLAGE_TAG = "rollDoorCamouflage";
    private static final String PANEL_CAMOUFLAGE_TAG = "rollDoorPanelCamouflage";
    private static final int NO_COLOR = -1;

    private int color = NO_COLOR;
    @Nullable
    private BlockState camouflage;
    @Nullable
    private BlockState panelCamouflage;

    public RollDoorBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.ROLLDOOR_BE.get(), pos, state);
    }

    @Nullable
    public static RollDoorBlockEntity findHeader(BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos).is(OpenSecurity.ROLLDOOR.get())
                && level.getBlockEntity(pos) instanceof RollDoorBlockEntity door) {
            return door;
        }
        for (int offset = 1; offset <= MAX_HEIGHT; offset++) {
            BlockPos candidate = pos.above(offset);
            if (!level.getBlockState(candidate).is(OpenSecurity.ROLLDOOR.get())) continue;
            return level.getBlockEntity(candidate) instanceof RollDoorBlockEntity door ? door : null;
        }
        return null;
    }

    @Nullable
    public static RollDoorBlockEntity at(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof RollDoorBlockEntity door ? door : null;
    }

    @Nullable
    public DyeColor getColor() {
        return color == NO_COLOR ? null : DyeColor.byId(color);
    }

    @Nullable
    public BlockState getCamouflage() {
        return camouflage;
    }

    public void setColor(DyeColor dye) {
        RollDoorBlockEntity header = getHeader();
        if (header != null && header != this) {
            header.setColor(dye);
            return;
        }
        color = dye.getId();
        setChanged();
        sync();
        propagatePanelAppearance();
    }

    public void clearCamouflage() {
        camouflage = null;
        setChanged();
        sync();
    }

    public void setCamouflage(BlockState state) {
        camouflage = validCamouflage(state);
        setChanged();
        sync();
    }

    public void setPanelCamouflage(@Nullable BlockState state) {
        RollDoorBlockEntity header = getHeader();
        if (header != null && header != this) {
            header.setPanelCamouflage(state);
            return;
        }
        panelCamouflage = validCamouflage(state);
        setChanged();
        sync();
        propagatePanelAppearance();
    }

    public void clearPanelCamouflage() {
        setPanelCamouflage(null);
    }

    public void copyPanelAppearanceFrom(RollDoorBlockEntity source) {
        color = source.color;
        camouflage = source.getPanelCamouflage();
        setChanged();
        sync();
    }

    @Nullable
    private BlockState getPanelCamouflage() {
        return getBlockState().is(OpenSecurity.ROLLDOOR.get()) ? panelCamouflage : camouflage;
    }

    @Nullable
    private RollDoorBlockEntity getHeader() {
        if (getBlockState().is(OpenSecurity.ROLLDOOR.get())) return this;
        return level == null ? null : findHeader(level, worldPosition);
    }

    private void propagatePanelAppearance() {
        if (level == null || level.isClientSide || !getBlockState().is(OpenSecurity.ROLLDOOR.get())) return;
        for (int offset = 1; offset <= MAX_HEIGHT; offset++) {
            BlockPos element = worldPosition.below(offset);
            if (!level.getBlockState(element).is(OpenSecurity.ROLLDOOR_ELEMENT.get())) break;
            if (level.getBlockEntity(element) instanceof RollDoorBlockEntity door) door.copyPanelAppearanceFrom(this);
        }
    }

    @Nullable
    private static BlockState validCamouflage(@Nullable BlockState state) {
        return state == null || state.isAir() || state.is(OpenSecurity.ROLLDOOR.get())
                || state.is(OpenSecurity.ROLLDOOR_ELEMENT.get()) ? null : state;
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        requestModelDataUpdate();
    }

    @Override
    public ModelData getModelData() {
        return camouflage == null ? ModelData.EMPTY : ModelData.of(CAMOUFLAGE, camouflage);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (color != NO_COLOR) tag.putInt(COLOR_TAG, color);
        if (camouflage != null) tag.put(CAMOUFLAGE_TAG, NbtUtils.writeBlockState(camouflage));
        if (panelCamouflage != null && getBlockState().is(OpenSecurity.ROLLDOOR.get())) {
            tag.put(PANEL_CAMOUFLAGE_TAG, NbtUtils.writeBlockState(panelCamouflage));
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        color = tag.contains(COLOR_TAG) ? Math.max(0, Math.min(DyeColor.values().length - 1, tag.getInt(COLOR_TAG))) : NO_COLOR;
        camouflage = null;
        panelCamouflage = null;
        if (tag.contains(CAMOUFLAGE_TAG, Tag.TAG_COMPOUND)) camouflage = readCamouflage(tag, CAMOUFLAGE_TAG, provider);
        if (getBlockState().is(OpenSecurity.ROLLDOOR.get())) {
            if (tag.contains(PANEL_CAMOUFLAGE_TAG, Tag.TAG_COMPOUND)) {
                panelCamouflage = readCamouflage(tag, PANEL_CAMOUFLAGE_TAG, provider);
            } else {
                // Preserve worlds/items written by the earlier single-camouflage implementation.
                panelCamouflage = camouflage;
            }
        }
        requestModelDataUpdate();
    }

    @Nullable
    private static BlockState readCamouflage(CompoundTag tag, String key, HolderLookup.Provider provider) {
        try {
            return validCamouflage(NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound(key)));
        } catch (RuntimeException ignored) {
            // Ignore invalid or removed camouflage blocks and fall back to the normal door model.
            return null;
        }
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
        refreshClientModel();
    }

    private void refreshClientModel() {
        requestModelDataUpdate();
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Nullable
    public static DyeColor dyeColor(ItemStack stack) {
        if (stack.isEmpty()) return null;
        for (DyeColor dye : DyeColor.values()) {
            if (stack.is(dye.getTag())) return dye;
        }
        return null;
    }

    public void loadFromItem(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        color = tag.contains(COLOR_TAG) ? Math.max(0, Math.min(DyeColor.values().length - 1, tag.getInt(COLOR_TAG))) : NO_COLOR;
        camouflage = null;
        panelCamouflage = null;
        if (tag.contains(CAMOUFLAGE_TAG, Tag.TAG_COMPOUND)) camouflage = readCamouflage(tag, CAMOUFLAGE_TAG);
        if (tag.contains(PANEL_CAMOUFLAGE_TAG, Tag.TAG_COMPOUND)) {
            panelCamouflage = readCamouflage(tag, PANEL_CAMOUFLAGE_TAG);
        } else {
            panelCamouflage = camouflage;
        }
        setChanged();
    }

    @Nullable
    private static BlockState readCamouflage(CompoundTag tag, String key) {
        try {
            return validCamouflage(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound(key)));
        } catch (RuntimeException ignored) {
            // Ignore invalid or removed camouflage blocks and fall back to the normal door model.
            return null;
        }
    }

    public void writeToItem(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (color == NO_COLOR) tag.remove(COLOR_TAG);
        else tag.putInt(COLOR_TAG, color);
        if (camouflage == null) tag.remove(CAMOUFLAGE_TAG);
        else tag.put(CAMOUFLAGE_TAG, NbtUtils.writeBlockState(camouflage));
        if (panelCamouflage == null) tag.remove(PANEL_CAMOUFLAGE_TAG);
        else tag.put(PANEL_CAMOUFLAGE_TAG, NbtUtils.writeBlockState(panelCamouflage));
        if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
