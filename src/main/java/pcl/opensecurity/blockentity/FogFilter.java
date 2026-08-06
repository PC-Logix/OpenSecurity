package pcl.opensecurity.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

final class FogFilter {
    private final Map<String, Set<String>> entries = new LinkedHashMap<>();

    void set(String type, String name, boolean enabled) {
        String normalized = type.toLowerCase();
        String entityName = name == null ? "" : name;
        if (enabled) entries.computeIfAbsent(normalized, ignored -> new LinkedHashSet<>()).add(entityName);
        else {
            Set<String> names = entries.get(normalized);
            if (names != null) {
                names.remove(entityName);
                if (names.isEmpty()) entries.remove(normalized);
            }
        }
    }

    boolean matches(Entity entity) {
        for (Map.Entry<String, Set<String>> entry : entries.entrySet()) {
            if (!matchesType(entry.getKey(), entity)) continue;
            if (entry.getValue().contains("") || entry.getValue().contains(entity.getName().getString())) return true;
        }
        return false;
    }

    Object[] table() {
        return entries.entrySet().stream().map(entry -> new Object[]{entry.getKey(), entry.getValue().toArray()}).toArray();
    }

    private boolean matchesType(String type, Entity entity) {
        return switch (type) {
            case "all" -> true;
            case "player" -> entity instanceof Player;
            case "hostile" -> entity instanceof Enemy;
            case "animal" -> entity instanceof Animal;
            case "item" -> entity instanceof ItemEntity;
            case "mob" -> entity instanceof Mob;
            default -> entity.getType().toShortString().equalsIgnoreCase(type)
                    || entity.getClass().getSimpleName().equalsIgnoreCase(type);
        };
    }

    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("count", entries.size());
        int index = 0;
        for (Map.Entry<String, Set<String>> entry : entries.entrySet()) {
            tag.putString("type" + index, entry.getKey());
            tag.putString("names" + index, String.join("\n", entry.getValue()));
            index++;
        }
        return tag;
    }

    void load(CompoundTag tag) {
        entries.clear();
        for (int index = 0; index < tag.getInt("count"); index++) {
            String type = tag.getString("type" + index);
            Set<String> names = new LinkedHashSet<>();
            String serialized = tag.getString("names" + index);
            if (serialized.isEmpty()) names.add("");
            else for (String name : serialized.split("\n", -1)) names.add(name);
            entries.put(type, names);
        }
    }
}
