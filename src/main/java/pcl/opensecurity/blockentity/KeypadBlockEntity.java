package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public final class KeypadBlockEntity extends SecurityBlockEntity {
    private static final int MAX_LABEL_LENGTH = 3;
    private static final int MAX_DISPLAY_LENGTH = 8;
    private boolean shouldBeep = true;
    private float volume = 1.0F;
    private String eventName = "keypad";
    private final String[] labels = {"1","2","3","4","5","6","7","8","9","*","0","#"};
    private final byte[] colors = {7,7,7,7,7,7,7,7,7,7,7,7};
    private String displayText = "";
    private byte displayColor = 7;

    public KeypadBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.KEYPAD_BE.get(), pos, state, "os_keypad", 32);
    }

    public int buttonAt(Direction facing, double hitX, double hitY, double hitZ) {
        double relX = switch (facing) {
            case NORTH -> hitX * 16.0;
            case SOUTH -> (1.0 - hitX) * 16.0;
            case WEST -> (1.0 - hitZ) * 16.0;
            case EAST -> hitZ * 16.0;
            default -> -1.0;
        };
        double relY = hitY * 16.0;
        if (relX < 4.0 || relX > 12.0 || relY < 2.0 || relY > 11.5) return -1;
        int col = (int) ((relX - 4.0) / 3.0);
        double colOffset = (relX - 4.0) % 3.0;
        int row = (int) ((relY - 2.0) / 2.5);
        double rowOffset = (relY - 2.0) % 2.5;
        if (colOffset > 2.0 || rowOffset > 2.0) return -1;
        int index = (2 - col) + 3 * (3 - row);
        return index >= 0 && index < 12 ? index : -1;
    }

    public void press(Player player, int index) {
        if (level == null || index < 0 || index >= labels.length) return;
        if (shouldBeep) level.playSound(null, worldPosition, OpenSecurity.KEYPAD_PRESS.get(), SoundSource.BLOCKS,
                Math.max(0.0F, volume) * 1.5F, 1.0F);
        node.sendToReachable("computer.signal", eventName, index + 1, labels[index]);
    }

    @Callback(doc = "function(name:string):boolean -- Sets the key press signal name.")
    public Object[] setEventName(Context context, Arguments args) {
        String name = args.checkString(0).trim();
        if (name.isEmpty()) return new Object[]{false, "event name cannot be empty"};
        eventName = name;
        setChanged();
        return new Object[]{true};
    }

    @Callback(doc = "function(enabled:boolean):boolean -- Enables or disables the key beep.")
    public Object[] setShouldBeep(Context context, Arguments args) {
        shouldBeep = args.checkBoolean(0);
        setChanged();
        return new Object[]{true};
    }

    @Callback(doc = "function(volume:number):boolean -- Sets keypad beep volume.")
    public Object[] setVolume(Context context, Arguments args) {
        volume = Math.max(0.0F, (float) args.checkDouble(0));
        setChanged();
        return new Object[]{true};
    }

    @Callback(direct = true, doc = "function():number -- Returns keypad beep volume.")
    public Object[] getVolume(Context context, Arguments args) {
        return new Object[]{(double) volume};
    }

    @Callback(doc = "function(text:string[,color:number]):boolean -- Sets display text and RGB bit color.")
    public Object[] setDisplay(Context context, Arguments args) {
        displayText = trim(args.checkString(0), MAX_DISPLAY_LENGTH);
        displayColor = (byte) (args.optInteger(1, displayColor) & 7);
        changedAndSync();
        return new Object[]{true};
    }

    @SuppressWarnings("rawtypes")
    @Callback(doc = "function(index:number,text:string[,color:number]) or function(labels:table[,colors:table])")
    public Object[] setKey(Context context, Arguments args) {
        if (args.isInteger(0)) {
            int index = args.checkInteger(0);
            if (index < 1 || index > 12) throw new IllegalArgumentException("index is out of range");
            labels[index - 1] = trim(args.checkString(1), MAX_LABEL_LENGTH);
            colors[index - 1] = (byte) (args.optInteger(2, colors[index - 1]) & 7);
        } else if (args.isTable(0)) {
            Map labelTable = args.checkTable(0);
            Map colorTable = args.optTable(1, null);
            for (int index = 1; index <= 12; index++) {
                Object label = labelTable.get(index);
                if (label instanceof String text) labels[index - 1] = trim(text, MAX_LABEL_LENGTH);
                Object color = colorTable == null ? null : colorTable.get(index);
                if (color instanceof Number number) colors[index - 1] = (byte) (number.intValue() & 7);
            }
        } else throw new IllegalArgumentException("first argument must be an index or table");
        changedAndSync();
        return new Object[]{true};
    }

    private void changedAndSync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private static String trim(String value, int length) {
        return value.length() <= length ? value : value.substring(0, length);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("eventName", eventName);
        tag.putBoolean("shouldBeep", shouldBeep);
        tag.putFloat("volume", volume);
        for (int index = 0; index < labels.length; index++) tag.putString("btn:" + index, labels[index]);
        tag.putByteArray("btn:colors", colors);
        tag.putString("fbText", displayText);
        tag.putByte("fbColor", displayColor);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        eventName = tag.contains("eventName") && !tag.getString("eventName").isBlank() ? tag.getString("eventName") : "keypad";
        shouldBeep = !tag.contains("shouldBeep") || tag.getBoolean("shouldBeep");
        volume = tag.contains("volume") ? Math.max(0.0F, tag.getFloat("volume")) : 1.0F;
        for (int index = 0; index < labels.length; index++) {
            if (tag.contains("btn:" + index)) labels[index] = trim(tag.getString("btn:" + index), MAX_LABEL_LENGTH);
        }
        byte[] savedColors = tag.getByteArray("btn:colors");
        System.arraycopy(savedColors, 0, colors, 0, Math.min(savedColors.length, colors.length));
        displayText = trim(tag.getString("fbText"), MAX_DISPLAY_LENGTH);
        displayColor = (byte) (tag.contains("fbColor") ? tag.getByte("fbColor") & 7 : 7);
    }
}
