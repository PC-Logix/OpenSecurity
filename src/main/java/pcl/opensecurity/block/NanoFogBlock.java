package pcl.opensecurity.block;

import pcl.opensecurity.blockentity.NanoFogBlockEntity;
import pcl.opensecurity.blockentity.NanoFogTerminalBlockEntity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class NanoFogBlock extends Block implements EntityBlock {
    public NanoFogBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends Block> codec() { return Block.CODEC; }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (level.getBlockEntity(pos) instanceof NanoFogBlockEntity fog
                && context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null
                && fog.canPass(entityContext.getEntity())) return Shapes.empty();
        return Shapes.block();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0.16, 0.16, 0.16, 15.84, 15.84, 15.84);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof NanoFogBlockEntity fog)) return;
        NanoFogTerminalBlockEntity terminal = fog.terminal();
        if (terminal == null) return;
        if (fog.shouldDamage(entity) && terminal.consumeDamageEnergy(fog.damage())) {
            entity.hurt(level.damageSources().magic(), fog.damage());
        }
        if (fog.knockback() > 0 && !fog.canPass(entity) && terminal.consumeKnockbackEnergy(fog.knockback())) {
            Vec3 away = entity.position().subtract(Vec3.atCenterOf(pos));
            if (away.lengthSqr() < 0.001) away = new Vec3(0, 1, 0);
            away = away.normalize().scale(fog.knockback());
            entity.push(away.x, away.y, away.z);
        }
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new NanoFogBlockEntity(pos, state); }
}
