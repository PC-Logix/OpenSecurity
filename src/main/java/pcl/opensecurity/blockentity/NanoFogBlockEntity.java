package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class NanoFogBlockEntity extends BlockEntity {
    private final FogFilter passFilter = new FogFilter();
    private final FogFilter damageFilter = new FogFilter();
    private boolean solid;
    private int knockback = 1;
    private int damage = 1;
    private BlockPos terminal;
    private ResourceLocation mimic = ResourceLocation.withDefaultNamespace("air");

    public NanoFogBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.NANOFOG_BE.get(), pos, state);
    }

    public void initialize(BlockPos terminal, ResourceLocation mimic) {
        this.terminal = terminal.immutable();
        this.mimic = mimic;
        changedAndSync();
    }

    public boolean canPass(Entity entity) { return !solid || passFilter.matches(entity); }
    public boolean shouldDamage(Entity entity) { return damage > 0 && damageFilter.matches(entity); }
    public int damage() { return damage; }
    public int knockback() { return knockback; }
    public void setSolid(boolean solid) { this.solid = solid; changedAndSync(); }
    public void setKnockback(int value) { knockback = Math.max(0, Math.min(2, value)); changedAndSync(); }
    public void setDamage(int value) { damage = Math.max(0, Math.min(5, value)); changedAndSync(); }
    public void setFilter(String type, String name, boolean pass, boolean damage) {
        passFilter.set(type, name, pass);
        damageFilter.set(type, name, damage);
        changedAndSync();
    }
    public Object[] passFilters() { return passFilter.table(); }
    public Object[] damageFilters() { return damageFilter.table(); }

    public NanoFogTerminalBlockEntity terminal() {
        return level != null && terminal != null && level.getBlockEntity(terminal) instanceof NanoFogTerminalBlockEntity value ? value : null;
    }

    private void changedAndSync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (terminal != null) tag.putLong("terminal", terminal.asLong());
        tag.putString("mimic", mimic.toString());
        tag.putBoolean("solid", solid);
        tag.putInt("knockback", knockback);
        tag.putInt("damageI", damage);
        tag.put("pass", passFilter.save());
        tag.put("damage", damageFilter.save());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        terminal = tag.contains("terminal") ? BlockPos.of(tag.getLong("terminal")) : null;
        ResourceLocation saved = ResourceLocation.tryParse(tag.getString("mimic"));
        mimic = saved == null ? ResourceLocation.withDefaultNamespace("air") : saved;
        solid = tag.getBoolean("solid");
        knockback = Math.max(0, Math.min(2, tag.contains("knockback") ? tag.getInt("knockback") : 1));
        damage = Math.max(0, Math.min(5, tag.contains("damageI") ? tag.getInt("damageI") : 1));
        passFilter.load(tag.getCompound("pass"));
        damageFilter.load(tag.getCompound("damage"));
    }
}
