package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import pcl.opensecurity.OpenSecurity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michi on 5/29/2017.
 */
public class TileEntityEntityDetector extends TileEntityOSBase {
    private int range = OpenSecurity.entityDetectorMaxRange;

    public TileEntityEntityDetector() {
        super("os_entdetector");
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(5 * OpenSecurity.entityDetectorMaxRange).create();
    }
    
    public TileEntityEntityDetector(EnvironmentHost host){
        super("os_entdetector", host);
    }

    // Thanks gamax92 from #oc for the following 2 methods...
    private HashMap<String, Object> info(Entity entity, BlockPos offset) {
        HashMap<String, Object> value = new HashMap<String, Object>();

        double rangeToEntity = entity.getDistance(getPos().getX(), getPos().getY(), getPos().getZ());
        String name;
        if (entity instanceof EntityPlayer)
            name = ((EntityPlayer) entity).getDisplayNameString();
        else
            name = entity.getName();

        BlockPos entityLocalPosition = entity.getPosition().subtract(offset);

        value.put("name", name);
        value.put("range", rangeToEntity);
        value.put("x", entityLocalPosition.getX());
        value.put("y", entityLocalPosition.getY());
        value.put("z", entityLocalPosition.getZ());
        node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity, entityLocalPosition.getX(), entityLocalPosition.getY(), entityLocalPosition.getZ());

        return value;
    }

    private Map<Integer, HashMap<String, Object>> scan(boolean players, BlockPos offset) {
        Map<Integer, HashMap<String, Object>> output = new HashMap<>();
        int index = 1;
        for(Entity entity : getWorld().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos(), getPos()).grow(range))){
            if(players && entity instanceof EntityPlayer) {
                output.put(index++, info(entity, offset));
            } else if(!players && !(entity instanceof EntityPlayer)) {
                output.put(index++, info(entity, offset));
            }
        }
        return output;
    }

    @Callback(doc = "function(optional:int:range):table; pushes a signal \"entityDetect\" for each player in range, optional set range.")
    public Object[] scanPlayers(Context context, Arguments args) {

        range = Math.min(OpenSecurity.entityDetectorMaxRange, args.optInteger(0, range));

        if(!consumeEnergy(range))
            return new Object[] { false, "Not enough power in OC Network." };

        return new Object[]{ scan(true, getPos()) };
    }

    @Callback(doc = "function(optional:int:range):table; pushes a signal \"entityDetect\" for each entity in range (excluding players), optional set range.")
    public Object[] scanEntities(Context context, Arguments args) {

        range = Math.min(OpenSecurity.entityDetectorMaxRange, args.optInteger(0, range));

        if(!consumeEnergy(range))
            return new Object[] { false, "Not enough power in OC Network." };

        return new Object[]{ scan(false, getPos()) };
    }

    private boolean consumeEnergy(int range){
        return node.changeBuffer(-5 * range) == 0;
    }

}
