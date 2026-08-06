package pcl.opensecurity.entity;

import pcl.opensecurity.blockentity.SecurityTerminalBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public final class EnergyBoltEntity extends ThrowableItemProjectile {
    private float damage = 5.0F;
    private int life = 100;

    public EnergyBoltEntity(EntityType<? extends EnergyBoltEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public void setDamage(float damage) {
        this.damage = Math.max(0, damage);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.GLOWSTONE_DUST;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && --life <= 0) discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            result.getEntity().hurt(level().damageSources().thrown(this, getOwner()), damage);
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (level().isClientSide) return;
        BlockPos pos = result.getBlockPos();
        BlockState state = level().getBlockState(pos);
        if (state.is(Blocks.GLASS) || state.getBlock() instanceof StainedGlassBlock
                || !state.getFluidState().isEmpty()) return;
        if ((state.canBeReplaced() || state.is(BlockTags.LEAVES))
                && !SecurityTerminalBlockEntity.isProtected(level(), pos, null)) {
            level().destroyBlock(pos, true);
        }
        if (!level().getBlockState(pos).isAir()) discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("damage", damage);
        tag.putInt("life", life);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.contains("damage") ? tag.getFloat("damage") : 5.0F;
        life = tag.contains("life") ? tag.getInt("life") : 100;
    }
}
