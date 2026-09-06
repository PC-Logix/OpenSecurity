package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.Config;
import pcl.opensecurity.data.CardData;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RFIDReaderBlockEntity extends SecurityBlockEntity {
    private String eventName = "rfidData";

    public RFIDReaderBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.RFID_READER_BE.get(), pos, state, "os_rfidreader", 32);
    }

    @Callback(doc = "function(name:string):boolean -- Sets the scan signal name.")
    public Object[] setEventName(Context context, Arguments args) {
        String requested = args.checkString(0).trim();
        if (requested.isEmpty()) return new Object[]{false, "event name cannot be empty"};
        eventName = requested;
        setChanged();
        return new Object[]{true};
    }

    @Callback(doc = "function([range:number]):table -- Scans nearby inventories and implanted RFID tags.")
    public Object[] scan(Context context, Arguments args) {
        if (level == null) return new Object[]{false, "world is unavailable"};
        int maxRange = Config.rfidMaxRange();
        int range = Math.max(1, Math.min(maxRange, args.optInteger(0, maxRange)));
        if (!consumeEnergy(5.0 * range)) return new Object[]{false, "not enough energy"};

        Map<Integer, Map<String, Object>> found = new LinkedHashMap<>();
        int index = 1;
        for (Entity entity : level.getEntities((Entity) null, new AABB(worldPosition).inflate(range))) {
            if (entity instanceof Player player) {
                index = scanInventory(found, index, player, player.getInventory());
            }
            CompoundTag tag = entity.getPersistentData().getCompound("rfidData");
            CardData card = CardData.read(tag);
            if (tag.contains("data") && card.valid()) found.put(index++, info(entity, card));
        }
        return new Object[]{found};
    }

    private int scanInventory(Map<Integer, Map<String, Object>> found, int index, Entity holder, Container inventory) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(OpenSecurity.RFID_CARD.get())) continue;
            CardData card = CardData.read(stack);
            if (card.valid()) found.put(index++, info(holder, card));
        }
        return index;
    }

    private Map<String, Object> info(Entity entity, CardData card) {
        double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5));
        String name = entity.getName().getString();
        String uuid = Config.exposedUuid(card.uuid());
        node.sendToReachable("computer.signal", eventName, name, distance, card.data(), uuid);
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("name", name);
        value.put("range", distance);
        value.put("data", card.data());
        value.put("uuid", uuid);
        value.put("locked", card.locked());
        return value;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putString("eventName", eventName);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        eventName = tag.contains("eventName") && !tag.getString("eventName").isBlank()
                ? tag.getString("eventName") : "rfidData";
    }
}
