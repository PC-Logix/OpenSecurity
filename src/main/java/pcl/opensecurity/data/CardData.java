package pcl.opensecurity.data;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.UUID;

public record CardData(String data, String uuid, boolean locked, int color) {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;

    public static CardData read(ItemStack stack) {
        return read(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public static CardData read(CompoundTag tag) {
        String uuid = tag.contains("uuid") && !tag.getString("uuid").isBlank()
                ? tag.getString("uuid") : UUID.randomUUID().toString();
        return new CardData(tag.getString("data"), uuid, tag.getBoolean("locked"),
                tag.contains("color") ? opaque(tag.getInt("color")) : DEFAULT_COLOR);
    }

    public boolean valid() {
        return !data.isEmpty();
    }

    public ItemStack write(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        tag.putString("data", data);
        tag.putString("uuid", uuid);
        tag.putBoolean("locked", locked);
        tag.putInt("color", opaque(color));
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    public CompoundTag write(CompoundTag tag) {
        tag.putString("data", data);
        tag.putString("uuid", uuid);
        tag.putBoolean("locked", locked);
        tag.putInt("color", opaque(color));
        return tag;
    }

    private static int opaque(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }
}
