package pcl.opensecurity.block;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.AlarmBlockEntity;
import pcl.opensecurity.blockentity.AlarmClientHooks;
import pcl.opensecurity.blockentity.BiometricReaderBlockEntity;
import pcl.opensecurity.blockentity.CardWriterBlockEntity;
import pcl.opensecurity.blockentity.DataBlockEntity;
import pcl.opensecurity.blockentity.DoorControllerBlockEntity;
import pcl.opensecurity.blockentity.EntityDetectorBlockEntity;
import pcl.opensecurity.blockentity.KeypadBlockEntity;
import pcl.opensecurity.blockentity.NanoFogTerminalBlockEntity;
import pcl.opensecurity.blockentity.RFIDReaderBlockEntity;
import pcl.opensecurity.blockentity.RollDoorControllerBlockEntity;
import pcl.opensecurity.blockentity.SecurityTerminalBlockEntity;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class SecurityBlock extends Block implements EntityBlock {
    public enum Kind { ALARM, BIOMETRIC_READER, CARD_WRITER, DATA_BLOCK, DOOR_CONTROLLER, ENTITY_DETECTOR, KEYPAD, NANOFOG_TERMINAL, RFID_READER, ROLLDOOR_CONTROLLER, SECURITY_TERMINAL }
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final Kind kind;

    public SecurityBlock(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return Block.CODEC;
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
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (kind == Kind.DOOR_CONTROLLER && player.isShiftKeyDown()
                && level.getBlockEntity(pos) instanceof DoorControllerBlockEntity controller) {
            if (!level.isClientSide && (player.getAbilities().instabuild || controller.canModify(player.getUUID()))) {
                controller.clearCamouflage();
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && kind == Kind.BIOMETRIC_READER
                && level.getBlockEntity(pos) instanceof BiometricReaderBlockEntity reader) {
            reader.read(player);
        }
        if (!level.isClientSide && kind == Kind.CARD_WRITER
                && level.getBlockEntity(pos) instanceof CardWriterBlockEntity writer && writer.extract(player)) {
            return InteractionResult.SUCCESS;
        }
        if (!level.isClientSide && kind == Kind.NANOFOG_TERMINAL
                && level.getBlockEntity(pos) instanceof NanoFogTerminalBlockEntity terminal && terminal.extract(player)) {
            return InteractionResult.SUCCESS;
        }
        if (kind == Kind.KEYPAD && hit.getDirection() == state.getValue(FACING)
                && !player.isShiftKeyDown() && level.getBlockEntity(pos) instanceof KeypadBlockEntity keypad) {
            int button = keypad.buttonAt(state.getValue(FACING), hit.getLocation().x - pos.getX(),
                    hit.getLocation().y - pos.getY(), hit.getLocation().z - pos.getZ());
            if (button >= 0) {
                if (!level.isClientSide) keypad.press(player, button);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return kind == Kind.BIOMETRIC_READER || kind == Kind.CARD_WRITER || kind == Kind.KEYPAD || kind == Kind.NANOFOG_TERMINAL
                ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (kind == Kind.DOOR_CONTROLLER && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() != OpenSecurity.DOOR_CONTROLLER.get()) {
            boolean success = level.isClientSide;
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof DoorControllerBlockEntity controller) {
                if (!player.getAbilities().instabuild && !controller.canModify(player.getUUID())) {
                    return ItemInteractionResult.sidedSuccess(false);
                }
                controller.setCamouflage(blockItem.getBlock().defaultBlockState());
                success = true;
            }
            if (success && !level.isClientSide && !player.getAbilities().instabuild) stack.shrink(1);
            return success ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.FAIL;
        }
        if (kind == Kind.CARD_WRITER && (stack.is(OpenSecurity.RFID_CARD.get()) || stack.is(OpenSecurity.MAG_CARD.get()))) {
            boolean success = level.isClientSide || level.getBlockEntity(pos) instanceof CardWriterBlockEntity writer
                    && writer.insert(stack);
            return success ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.FAIL;
        }
        if (kind == Kind.NANOFOG_TERMINAL && stack.is(OpenSecurity.NANODNA.get())) {
            boolean success = level.isClientSide || level.getBlockEntity(pos) instanceof NanoFogTerminalBlockEntity terminal
                    && terminal.insert(stack);
            return success ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.FAIL;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer == null) return;
        if (level.getBlockEntity(pos) instanceof DoorControllerBlockEntity controller) controller.setOwner(placer.getUUID());
        if (level.getBlockEntity(pos) instanceof SecurityTerminalBlockEntity terminal) terminal.setOwner(placer.getUUID());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof CardWriterBlockEntity writer) {
            writer.dropContents(level);
        }
        if (!state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof NanoFogTerminalBlockEntity terminal) terminal.removed();
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (kind) {
            case ALARM -> new AlarmBlockEntity(pos, state);
            case BIOMETRIC_READER -> new BiometricReaderBlockEntity(pos, state);
            case CARD_WRITER -> new CardWriterBlockEntity(pos, state);
            case DATA_BLOCK -> new DataBlockEntity(pos, state);
            case DOOR_CONTROLLER -> new DoorControllerBlockEntity(pos, state);
            case ENTITY_DETECTOR -> new EntityDetectorBlockEntity(pos, state);
            case KEYPAD -> new KeypadBlockEntity(pos, state);
            case NANOFOG_TERMINAL -> new NanoFogTerminalBlockEntity(pos, state);
            case RFID_READER -> new RFIDReaderBlockEntity(pos, state);
            case ROLLDOOR_CONTROLLER -> new RollDoorControllerBlockEntity(pos, state);
            case SECURITY_TERMINAL -> new SecurityTerminalBlockEntity(pos, state);
        };
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (kind == Kind.ALARM && type == OpenSecurity.ALARM_BE.get()) {
            if (level.isClientSide) {
                return (world, pos, currentState, blockEntity) -> AlarmClientHooks.tick((AlarmBlockEntity) blockEntity);
            }
            return null;
        }
        if (kind == Kind.SECURITY_TERMINAL && type == OpenSecurity.SECURITY_TERMINAL_BE.get()) {
            return (world, pos, currentState, blockEntity) -> ((SecurityTerminalBlockEntity) blockEntity).serverTick();
        }
        if (kind == Kind.ROLLDOOR_CONTROLLER && type == OpenSecurity.ROLLDOOR_CONTROLLER_BE.get()) {
            return (world, pos, currentState, blockEntity) -> ((RollDoorControllerBlockEntity) blockEntity).serverTick();
        }
        if (kind == Kind.NANOFOG_TERMINAL && type == OpenSecurity.NANOFOG_TERMINAL_BE.get()) {
            return (world, pos, currentState, blockEntity) -> ((NanoFogTerminalBlockEntity) blockEntity).serverTick();
        }
        return null;
    }
}
