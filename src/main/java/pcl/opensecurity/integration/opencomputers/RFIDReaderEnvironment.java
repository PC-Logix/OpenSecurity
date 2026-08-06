package pcl.opensecurity.integration.opencomputers;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.data.CardData;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RFIDReaderEnvironment extends AbstractManagedEnvironment {
    private static final int MAX_RANGE = 16;
    private final EnvironmentHost host;

    public RFIDReaderEnvironment(EnvironmentHost host) {
        this.host = host;
        setNode(Network.newNode(this, Visibility.Network).withComponent("os_rfidreader").withConnector(32).create());
    }

    @Callback(doc = "function([range:number]):table -- Scans nearby inventories and implanted RFID tags.")
    public Object[] scan(Context context, Arguments args) {
        Level level = host.getEnvironmentLevel();
        if (level == null) return new Object[]{false, "world is unavailable"};
        int range = Math.max(1, Math.min(MAX_RANGE, args.optInteger(0, MAX_RANGE)));
        if (node() instanceof Connector connector && !connector.tryChangeBuffer(-5.0 * range)) {
            return new Object[]{false, "not enough energy"};
        }

        Map<Integer, Map<String, Object>> found = new LinkedHashMap<>();
        int index = 1;
        AABB bounds = new AABB(host.xPosition(), host.yPosition(), host.zPosition(),
                host.xPosition(), host.yPosition(), host.zPosition()).inflate(range);
        for (Entity entity : level.getEntities((Entity) null, bounds)) {
            if (entity instanceof Player player) index = scanInventory(found, index, player, player.getInventory());
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
        double dx = entity.getX() - host.xPosition();
        double dy = entity.getY() - host.yPosition();
        double dz = entity.getZ() - host.zPosition();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        String name = entity.getName().getString();
        node().sendToReachable("computer.signal", "rfidData", name, distance, card.data(), card.uuid());
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("name", name);
        value.put("range", distance);
        value.put("data", card.data());
        value.put("uuid", card.uuid());
        value.put("locked", card.locked());
        return value;
    }
}
