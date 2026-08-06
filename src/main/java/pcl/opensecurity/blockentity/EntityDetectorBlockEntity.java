package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntityDetectorBlockEntity extends SecurityBlockEntity {
    private static final int MAX_RANGE = 16;
    private int range = MAX_RANGE;

    public EntityDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.ENTITY_DETECTOR_BE.get(), pos, state, "os_entdetector", 5.0 * MAX_RANGE);
    }

    @Callback(doc = "function([range:number]):table -- Scans for players and emits entityDetect signals.")
    public Object[] scanPlayers(Context context, Arguments args) {
        return scan(args, true);
    }

    @Callback(doc = "function([range:number]):table -- Scans for non-player entities and emits entityDetect signals.")
    public Object[] scanEntities(Context context, Arguments args) {
        return scan(args, false);
    }

    private Object[] scan(Arguments args, boolean players) {
        if (level == null) return new Object[]{false, "world is unavailable"};
        range = Math.max(1, Math.min(MAX_RANGE, args.optInteger(0, range)));
        if (!consumeEnergy(5.0 * range)) return new Object[]{false, "not enough energy"};

        Map<Integer, Map<String, Object>> output = new LinkedHashMap<>();
        int index = 1;
        for (Entity entity : level.getEntities((Entity) null, new AABB(worldPosition).inflate(range),
                entity -> players == (entity instanceof Player))) {
            Map<String, Object> info = info(entity);
            output.put(index++, info);
            node.sendToReachable("computer.signal", "entityDetect", info.get("name"), info.get("range"),
                    info.get("x"), info.get("y"), info.get("z"));
        }
        return new Object[]{output};
    }

    private Map<String, Object> info(Entity entity) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("name", entity.getName().getString());
        value.put("uuid", entity.getUUID().toString());
        value.put("range", Math.sqrt(entity.distanceToSqr(worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5)));
        value.put("height", entity.getBbHeight());
        value.put("x", entity.getX() - worldPosition.getX());
        value.put("y", entity.getY() - worldPosition.getY());
        value.put("z", entity.getZ() - worldPosition.getZ());
        return value;
    }
}
