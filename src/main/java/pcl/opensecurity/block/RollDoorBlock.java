package pcl.opensecurity.block;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.RollDoorBlockEntity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class RollDoorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<RollDoorBlock> CODEC = simpleCodec(RollDoorBlock::new);

    public RollDoorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(OpenSecurity.ROLLDOOR.get()) || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        RollDoorBlockEntity door = RollDoorBlockEntity.at(level, pos);
        if (door == null) return InteractionResult.PASS;
        if (!level.isClientSide) door.clearCamouflage();
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        return applyAppearanceItem(stack, level, pos, player);
    }

    static ItemInteractionResult applyAppearanceItem(ItemStack stack, Level level, BlockPos pos, Player player) {
        RollDoorBlockEntity door = RollDoorBlockEntity.at(level, pos);
        if (door == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        DyeColor dye = RollDoorBlockEntity.dyeColor(stack);
        if (dye != null) {
            if (!level.isClientSide) {
                door.setColor(dye);
                if (!player.getAbilities().instabuild) stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() != OpenSecurity.ROLLDOOR.get()
                && blockItem.getBlock() != OpenSecurity.ROLLDOOR_ELEMENT.get()) {
            if (!level.isClientSide) {
                if (door.getBlockState().is(OpenSecurity.ROLLDOOR.get())) {
                    door.setCamouflage(blockItem.getBlock().defaultBlockState());
                } else {
                    door.setPanelCamouflage(blockItem.getBlock().defaultBlockState());
                }
                if (!player.getAbilities().instabuild) stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof RollDoorBlockEntity door) door.loadFromItem(stack);
    }

    @Override
    public java.util.List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        java.util.List<ItemStack> drops = super.getDrops(state, builder);
        if (builder.getOptionalParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.BLOCK_ENTITY)
                instanceof RollDoorBlockEntity door) {
            drops.stream().filter(stack -> stack.is(OpenSecurity.ROLLDOOR_ITEM.get())).forEach(door::writeToItem);
        }
        return drops;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RollDoorBlockEntity(pos, state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
