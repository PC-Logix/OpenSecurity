package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.Config;
import pcl.opensecurity.entity.NanoFogSwarmEntity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class NanoFogTerminalBlockEntity extends SecurityBlockEntity {
    private static final int RANGE = 32;
    private static final int BLOCK_LIMIT = 256;
    private static final int BUILD_COST = 50;
    private final ItemStackHandler inventory = new ItemStackHandler(7) {
        @Override public boolean isItemValid(int slot, ItemStack stack) { return slot == 0 && stack.is(OpenSecurity.NANODNA.get()); }
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };
    private final Set<BlockPos> fogBlocks = new LinkedHashSet<>();
    private int ticks;

    public NanoFogTerminalBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.NANOFOG_TERMINAL_BE.get(), pos, state, "os_nanofog_terminal", 512);
    }

    public ItemStackHandler inventory() { return inventory; }

    public boolean insert(ItemStack held) {
        if (!inventory.getStackInSlot(0).isEmpty() || !held.is(OpenSecurity.NANODNA.get())) return false;
        inventory.setStackInSlot(0, held.split(held.getCount()));
        return true;
    }

    public boolean extract(Player player) {
        for (int slot = 1; slot >= 0; slot--) {
            ItemStack stack = inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false);
            if (!stack.isEmpty()) {
                if (!player.addItem(stack)) player.drop(stack, false);
                return true;
            }
        }
        return false;
    }

    public void serverTick() {
        if (level == null || level.isClientSide || ++ticks < 100) return;
        ticks = 0;
        fogBlocks.removeIf(pos -> !(level.getBlockEntity(pos) instanceof NanoFogBlockEntity));
        if (!fogBlocks.isEmpty() && !consumeEnergy(fogBlocks.size())) resetAllBlocks();
    }

    public boolean consumeKnockbackEnergy(int power) { return consumeEnergy(2.0 * power); }
    public boolean consumeDamageEnergy(int power) { return consumeEnergy(5.0 * power); }

    public void removed() {
        resetAllBlocks();
        if (level == null) return;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.extractItem(slot, inventory.getStackInSlot(slot).getCount(), false);
            if (!stack.isEmpty()) level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack));
        }
    }

    @Callback(direct = true, doc = "function():item -- Returns the NanoDNA material buffer.")
    public Object[] getMaterial(Context context, Arguments args) { return new Object[]{inventory.getStackInSlot(0)}; }

    @Callback(direct = true, doc = "function():table -- Returns fog block locations.")
    public Object[] getBlocks(Context context, Arguments args) { return new Object[]{fogBlocks.toArray()}; }

    @Callback(doc = "function(x:number,y:number,z:number,material:string):boolean,string -- Places or updates fog.")
    public Object[] set(Context context, Arguments args) {
        BlockPos pos = relativePos(args, 0);
        if (pos == null) return new Object[]{false, "invalid arguments"};
        return setBlock(pos, args.checkString(3));
    }

    @Callback(doc = "function(x1,y1,z1,x2,y2,z2,material:string):table -- Places or updates an area.")
    public Object[] setArea(Context context, Arguments args) {
        BlockPos a = relativePos(args, 0), b = relativePos(args, 3);
        if (a == null || b == null) return new Object[]{false, "invalid arguments"};
        String material = args.checkString(6);
        List<Object> results = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(a, b)) {
            if (results.size() >= BLOCK_LIMIT) break;
            results.add(setBlock(pos.immutable(), material));
        }
        return new Object[]{results.toArray()};
    }

    @Callback(doc = "function(x1,y1,z1,x2,y2,z2):table -- Removes fog in an area.")
    public Object[] resetArea(Context context, Arguments args) {
        BlockPos a = relativePos(args, 0), b = relativePos(args, 3);
        if (a == null || b == null) return new Object[]{false, "invalid arguments"};
        List<Boolean> results = new ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(a, b)) results.add(removeBlock(pos.immutable()));
        return new Object[]{results.toArray()};
    }

    @Callback(direct = true, doc = "function(x,y,z):boolean -- Makes fog solid.")
    public Object[] setSolid(Context context, Arguments args) { return updateFog(args, fog -> fog.setSolid(true)); }

    @Callback(direct = true, doc = "function(x,y,z):boolean -- Makes fog a passable shield.")
    public Object[] setShield(Context context, Arguments args) { return updateFog(args, fog -> fog.setSolid(false)); }

    @Callback(direct = true, doc = "function(x,y,z,power):boolean -- Sets knockback 0 through 2.")
    public Object[] setKnockback(Context context, Arguments args) { return updateFog(args, fog -> fog.setKnockback(args.checkInteger(3))); }

    @Callback(direct = true, doc = "function(x,y,z,power):boolean -- Sets damage 0 through 5.")
    public Object[] setDamage(Context context, Arguments args) { return updateFog(args, fog -> fog.setDamage(args.checkInteger(3))); }

    @Callback(direct = true, doc = "function():table -- Lists filter entity identifiers.")
    public Object[] getEntityClassNames(Context context, Arguments args) {
        return new Object[]{BuiltInRegistries.ENTITY_TYPE.keySet().stream().map(ResourceLocation::toString).toArray()};
    }

    @Callback(direct = true, doc = "function(x,y,z,type,pass[,damage,name]):boolean -- Changes a fog filter.")
    public Object[] setFilter(Context context, Arguments args) {
        NanoFogBlockEntity fog = fog(relativePos(args, 0));
        if (fog == null) return new Object[]{false};
        fog.setFilter(args.checkString(3), args.optString(6, ""), args.checkBoolean(4), args.optBoolean(5, false));
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function(x,y,z):table -- Returns pass filters.")
    public Object[] getFilterPass(Context context, Arguments args) {
        NanoFogBlockEntity fog = fog(relativePos(args, 0));
        return fog == null ? new Object[]{false, "no fog block"} : fog.passFilters();
    }

    @Callback(direct = true, doc = "function(x,y,z):table -- Returns damage filters.")
    public Object[] getFilterDamage(Context context, Arguments args) {
        NanoFogBlockEntity fog = fog(relativePos(args, 0));
        return fog == null ? new Object[]{false, "no fog block"} : fog.damageFilters();
    }

    @Callback(direct = true, doc = "function(x,y,z):string -- Returns the block type.")
    public Object[] get(Context context, Arguments args) {
        BlockPos pos = relativePos(args, 0);
        if (pos == null || level == null) return new Object[]{"unknown"};
        if (level.getBlockEntity(pos) instanceof NanoFogBlockEntity) return new Object[]{"nanoFog"};
        return new Object[]{level.getBlockState(pos).isAir() ? "air" : BuiltInRegistries.BLOCK.getKey(level.getBlockState(pos).getBlock()).toString()};
    }

    @Callback(doc = "function(x,y,z):boolean -- Disassembles one fog block.")
    public Object[] reset(Context context, Arguments args) { return new Object[]{removeBlock(relativePos(args, 0))}; }

    @Callback(doc = "function():boolean -- Disassembles all fog blocks.")
    public Object[] resetAll(Context context, Arguments args) { resetAllBlocks(); return new Object[]{fogBlocks.isEmpty()}; }

    private Object[] setBlock(BlockPos pos, String material) {
        if (level == null || distance(pos) > RANGE) return new Object[]{false, "block out of range"};
        ResourceLocation mimic = ResourceLocation.tryParse(material);
        if (mimic == null || !BuiltInRegistries.BLOCK.containsKey(mimic)) return new Object[]{false, "unknown material"};
        if (level.getBlockEntity(pos) instanceof NanoFogBlockEntity existing) {
            existing.initialize(worldPosition, mimic);
            fogBlocks.add(pos);
            setChanged();
            return new Object[]{true};
        }
        if (!level.getBlockState(pos).isAir()) return new Object[]{false, "target block is not air"};
        if (fogBlocks.size() >= BLOCK_LIMIT) return new Object[]{false, "block limit reached"};
        ItemStack dna = inventory.getStackInSlot(0);
        if (dna.isEmpty() || !dna.is(OpenSecurity.NANODNA.get())) return new Object[]{false, "no NanoDNA"};
        if (!consumeEnergy(BUILD_COST)) return new Object[]{false, "not enough energy"};
        dna.shrink(1);
        inventory.setStackInSlot(0, dna);
        if (!Config.instantNanoFog()) {
            NanoFogSwarmEntity swarm = new NanoFogSwarmEntity(OpenSecurity.NANO_FOG_SWARM.get(), level);
            swarm.configure(worldPosition, pos, mimic);
            level.addFreshEntity(swarm);
            return new Object[]{true};
        }
        finishSwarmBuild(pos, mimic);
        return new Object[]{true};
    }

    public void finishSwarmBuild(BlockPos pos, ResourceLocation mimic) {
        if (level == null || !level.getBlockState(pos).isAir() || fogBlocks.size() >= BLOCK_LIMIT) {
            refundNanoDna();
            return;
        }
        level.setBlock(pos, OpenSecurity.NANOFOG.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof NanoFogBlockEntity fog) fog.initialize(worldPosition, mimic);
        fogBlocks.add(pos.immutable());
        setChanged();
    }

    private void refundNanoDna() {
        if (level == null) return;
        ItemStack remainder = inventory.insertItem(0, new ItemStack(OpenSecurity.NANODNA.get()), false);
        if (!remainder.isEmpty()) remainder = inventory.insertItem(1, remainder, false);
        if (!remainder.isEmpty()) level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, remainder));
    }

    private boolean removeBlock(BlockPos pos) {
        if (level == null || pos == null || !fogBlocks.remove(pos)) return false;
        if (level.getBlockState(pos).is(OpenSecurity.NANOFOG.get())) level.removeBlock(pos, false);
        ItemStack dna = new ItemStack(OpenSecurity.NANODNA.get());
        ItemStack remainder = inventory.insertItem(0, dna, false);
        if (!remainder.isEmpty()) remainder = inventory.insertItem(1, remainder, false);
        if (!remainder.isEmpty()) level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, remainder));
        setChanged();
        return true;
    }

    private void resetAllBlocks() {
        for (BlockPos pos : List.copyOf(fogBlocks)) removeBlock(pos);
    }

    private Object[] updateFog(Arguments args, java.util.function.Consumer<NanoFogBlockEntity> action) {
        NanoFogBlockEntity fog = fog(relativePos(args, 0));
        if (fog == null) return new Object[]{false};
        action.accept(fog);
        return new Object[]{true};
    }

    private NanoFogBlockEntity fog(BlockPos pos) {
        return level != null && pos != null && level.getBlockEntity(pos) instanceof NanoFogBlockEntity fog ? fog : null;
    }

    private BlockPos relativePos(Arguments args, int offset) {
        if (args.count() < offset + 3) return null;
        return worldPosition.offset(args.checkInteger(offset), args.checkInteger(offset + 1), args.checkInteger(offset + 2));
    }

    private double distance(BlockPos pos) {
        return Math.sqrt(worldPosition.distSqr(pos));
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));
        tag.putInt("fogCount", fogBlocks.size());
        int index = 0;
        for (BlockPos pos : fogBlocks) tag.putLong("fog" + index++, pos.asLong());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inventory")) inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        fogBlocks.clear();
        for (int index = 0; index < tag.getInt("fogCount"); index++) fogBlocks.add(BlockPos.of(tag.getLong("fog" + index)));
    }
}
