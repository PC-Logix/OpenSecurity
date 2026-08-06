package pcl.opensecurity.block;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.SecureDoorBlockEntity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;

public final class SecureDoorBlock extends DoorBlock implements EntityBlock {
    public SecureDoorBlock(Properties properties) {
        super(BlockSetType.IRON, properties.noOcclusion());
    }

    @Override
    public MapCodec<? extends DoorBlock> codec() {
        return DoorBlock.CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new SecureDoorBlockEntity(pos, state) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null && level.getBlockEntity(lowerPos(pos, state)) instanceof SecureDoorBlockEntity door) {
            door.setOwner(placer.getUUID());
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(OpenSecurity.MAG_CARD.get())) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        BlockPos lower = lowerPos(pos, state);
        boolean success = level.isClientSide || level.getBlockEntity(lower) instanceof SecureDoorBlockEntity door
                && door.swipe(stack, player, hit.getDirection());
        return success ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.FAIL;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor,
                                   BlockPos neighborPos, boolean movedByPiston) {
        // Secure doors are controlled by their OC controller, never by redstone.
    }

    public static BlockPos lowerPos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }
}
