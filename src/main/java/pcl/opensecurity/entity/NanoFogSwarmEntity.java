package pcl.opensecurity.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import pcl.opensecurity.Config;
import pcl.opensecurity.blockentity.NanoFogTerminalBlockEntity;

/** Carries one NanoDNA charge from a terminal to the block it will assemble. */
public final class NanoFogSwarmEntity extends Entity {
    private BlockPos terminal = BlockPos.ZERO;
    private BlockPos target = BlockPos.ZERO;
    private ResourceLocation mimic = ResourceLocation.withDefaultNamespace("air");

    public NanoFogSwarmEntity(EntityType<? extends NanoFogSwarmEntity> type, Level level) {
        super(type, level);
        noPhysics = true;
    }

    public void configure(BlockPos terminal, BlockPos target, ResourceLocation mimic) {
        this.terminal = terminal.immutable();
        this.target = target.immutable();
        this.mimic = mimic;
        setPos(terminal.getX() + 0.5, terminal.getY() + 0.5, terminal.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            for (int i = 0; i < Config.nanoFogSwarmResolution(); i++) {
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + (random.nextDouble() - 0.5) * 0.35,
                        getY() + (random.nextDouble() - 0.5) * 0.35,
                        getZ() + (random.nextDouble() - 0.5) * 0.35,
                        0, 0, 0);
            }
            return;
        }

        Vec3 destination = Vec3.atCenterOf(target);
        Vec3 remaining = destination.subtract(position());
        if (remaining.lengthSqr() <= 0.16 || tickCount >= 20 * 30) {
            if (level().getBlockEntity(terminal) instanceof NanoFogTerminalBlockEntity terminalEntity) {
                terminalEntity.finishSwarmBuild(target, mimic);
            }
            discard();
            return;
        }
        setDeltaMovement(remaining.normalize().scale(0.35));
        move(MoverType.SELF, getDeltaMovement());
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        terminal = BlockPos.of(tag.getLong("terminal"));
        target = BlockPos.of(tag.getLong("target"));
        ResourceLocation loaded = ResourceLocation.tryParse(tag.getString("mimic"));
        mimic = loaded == null ? ResourceLocation.withDefaultNamespace("air") : loaded;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putLong("terminal", terminal.asLong());
        tag.putLong("target", target.asLong());
        tag.putString("mimic", mimic.toString());
    }
}
