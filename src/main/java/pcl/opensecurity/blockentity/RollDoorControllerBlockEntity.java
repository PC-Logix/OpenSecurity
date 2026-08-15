package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.block.RollDoorBlock;
import pcl.opensecurity.block.RollDoorElementBlock;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RollDoorControllerBlockEntity extends SecurityBlockEntity {
    private static final int MAX_HEIGHT = 15;
    private static final double MIN_SPEED = 0.1;
    private static final double MAX_SPEED = 3.0;
    private String password = "";
    private double position;
    private double target = -1;
    private double speed = 1.0;

    public RollDoorControllerBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.ROLLDOOR_CONTROLLER_BE.get(), pos, state, "os_rolldoorcontroller", 32);
    }

    public void serverTick() {
        if (level == null || level.isClientSide || target < 0) return;
        double step = speed / 20.0;
        if (Math.abs(target - position) <= step) {
            position = target;
            target = -1;
        } else position += Math.copySign(step, target - position);
        applyPosition();
        setChanged();
    }

    @Callback(doc = "function():boolean -- Returns true when fully open.")
    public Object[] isOpen(Context context, Arguments args) {
        return new Object[]{position <= 0.0};
    }

    @Callback(direct = true, doc = "function():boolean -- Returns true while moving.")
    public Object[] isMoving(Context context, Arguments args) {
        return new Object[]{target >= 0.0};
    }

    @Callback(doc = "function():number -- Returns current vertical position.")
    public Object[] getPosition(Context context, Arguments args) {
        return new Object[]{position};
    }

    @Callback(doc = "function(position:number[,password:string]):number -- Moves to a position.")
    public Object[] setPosition(Context context, Arguments args) {
        if (!password.equals(args.optString(1, ""))) return new Object[]{false, "invalid password"};
        setTarget(Math.max(0, Math.min(maxHeight(), args.checkDouble(0))));
        return new Object[]{target};
    }

    @Callback(doc = "function([password:string]):boolean,string -- Toggles open or closed.")
    public Object[] toggle(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        if (target >= 0) return new Object[]{false, "rolldoor is moving"};
        setTarget(position <= 0 ? maxHeight() : 0);
        return new Object[]{true, position <= 0 ? "closing door" : "opening door"};
    }

    @Callback(doc = "function([password:string]):boolean -- Opens the roll door.")
    public Object[] open(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        setTarget(0);
        return new Object[]{true};
    }

    @Callback(doc = "function([password:string]):boolean -- Closes the roll door.")
    public Object[] close(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        setTarget(maxHeight());
        return new Object[]{true};
    }

    @Callback(doc = "function([password:string]):number -- Raises the roll door by one panel.")
    public Object[] moveUp(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        return moveBy(-1);
    }

    @Callback(doc = "function([password:string]):number -- Lowers the roll door by one panel.")
    public Object[] moveDown(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        return moveBy(1);
    }

    @Callback(direct = true, doc = "function(speed:number[,password:string]):number -- Sets movement speed.")
    public Object[] setSpeed(Context context, Arguments args) {
        if (!password.equals(args.optString(1, ""))) return new Object[]{false, "invalid password"};
        speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, args.checkDouble(0)));
        setChanged();
        return new Object[]{speed};
    }

    @Callback(doc = "function([password:string]):table -- Recalculates column heights.")
    public Object[] calibrate(Context context, Arguments args) {
        if (!password.equals(args.optString(0, ""))) return new Object[]{false, "invalid password"};
        position = Math.min(position, maxHeight());
        applyPosition();
        return getHeight(context, args);
    }

    @Callback(direct = true, doc = "function():table -- Returns the height of each connected column.")
    public Object[] getHeight(Context context, Arguments args) {
        List<Integer> heights = new ArrayList<>();
        for (BlockPos header : headers()) heights.add(height(header));
        return new Object[]{heights.toArray()};
    }

    @Callback(doc = "function(newPassword:string[,oldPassword:string]):boolean -- Changes the password.")
    public Object[] setPassword(Context context, Arguments args) {
        if (!password.equals(args.optString(1, ""))) return new Object[]{false, "old password doesn't match"};
        password = args.checkString(0);
        setChanged();
        return new Object[]{true};
    }

    private void setTarget(double requested) {
        target = requested;
        if (Math.abs(target - position) < 0.0001) target = -1;
        setChanged();
    }

    private Object[] moveBy(int panels) {
        double base = target < 0 ? position : target;
        setTarget(Math.max(0, Math.min(maxHeight(), base + panels)));
        return new Object[]{target < 0 ? position : target};
    }

    private int maxHeight() {
        int height = 0;
        for (BlockPos header : headers()) height = Math.max(height, height(header));
        return height;
    }

    private int height(BlockPos header) {
        int height = 0;
        for (int offset = 1; offset <= MAX_HEIGHT; offset++) {
            BlockState state = level.getBlockState(header.below(offset));
            if (!state.isAir() && !state.is(OpenSecurity.ROLLDOOR_ELEMENT.get())) break;
            height++;
        }
        return height;
    }

    private Set<BlockPos> headers() {
        Set<BlockPos> result = new LinkedHashSet<>();
        if (level == null) return result;
        BlockPos first = null;
        for (Direction direction : Direction.values()) {
            BlockPos candidate = worldPosition.relative(direction);
            if (level.getBlockState(candidate).is(OpenSecurity.ROLLDOOR.get())) {
                first = candidate;
                break;
            }
        }
        if (first == null) return result;
        result.add(first);
        Direction facing = level.getBlockState(first).getValue(RollDoorBlock.FACING);
        Direction across = facing.getClockWise();
        for (Direction direction : new Direction[]{across, across.getOpposite()}) {
            for (int offset = 1; offset < MAX_HEIGHT; offset++) {
                BlockPos candidate = first.relative(direction, offset);
                BlockState state = level.getBlockState(candidate);
                if (!state.is(OpenSecurity.ROLLDOOR.get()) || state.getValue(RollDoorBlock.FACING) != facing) break;
                result.add(candidate);
            }
        }
        return result;
    }

    private void applyPosition() {
        if (level == null) return;
        int visible = (int) Math.ceil(position);
        for (BlockPos header : headers()) {
            Direction facing = level.getBlockState(header).getValue(RollDoorBlock.FACING);
            int height = height(header);
            for (int offset = 1; offset <= height; offset++) {
                BlockPos element = header.below(offset);
                BlockState state = level.getBlockState(element);
                if (offset <= visible && (state.isAir() || state.is(OpenSecurity.ROLLDOOR_ELEMENT.get()))) {
                    level.setBlock(element, OpenSecurity.ROLLDOOR_ELEMENT.get().defaultBlockState()
                            .setValue(RollDoorElementBlock.FACING, facing), 3);
                    if (level.getBlockEntity(element) instanceof RollDoorBlockEntity elementDoor
                            && level.getBlockEntity(header) instanceof RollDoorBlockEntity headerDoor) {
                        elementDoor.copyPanelAppearanceFrom(headerDoor);
                    }
                } else if (offset > visible && state.is(OpenSecurity.ROLLDOOR_ELEMENT.get())) {
                    level.removeBlock(element, false);
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("password", password);
        tag.putDouble("position", position);
        tag.putDouble("targetPos", target);
        tag.putDouble("moveSpeed", speed);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        password = tag.getString("password");
        position = Math.max(0, tag.getDouble("position"));
        target = tag.contains("targetPos") ? tag.getDouble("targetPos") : -1;
        speed = Math.max(MIN_SPEED, Math.min(MAX_SPEED, tag.contains("moveSpeed") ? tag.getDouble("moveSpeed") : 1));
    }
}
