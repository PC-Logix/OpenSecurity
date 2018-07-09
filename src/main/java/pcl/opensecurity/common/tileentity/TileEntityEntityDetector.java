package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.SoundHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Michi on 5/29/2017.
 */
public class TileEntityEntityDetector extends TileEntityOSBase {

    public int range = OpenSecurity.rfidRange;
    public boolean offset = false;

    public TileEntityEntityDetector() {
        node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
    }

    private static String getComponentName() {
        return "os_entdetector";
    }

    // Thanks gamax92 from #oc for the following 2 methods...
    private HashMap<String, Object> info(Entity entity, boolean offset) {
        HashMap<String, Object> value = new HashMap<String, Object>();

        double rangeToEntity = entity.getDistance(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
        String name;
        if (entity instanceof EntityPlayerMP)
            name = ((EntityPlayer) entity).getDisplayNameString();
        else
            name = entity.getName();
        node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity);
        value.put("name", name);
        value.put("range", rangeToEntity);
        if (!offset) {
            value.put("x", entity.posX);
            value.put("y", entity.posY);
            value.put("z", entity.posZ);
        } else {
            value.put("x", entity.posX - this.getPos().getX());
            value.put("y", entity.posY - this.getPos().getY());
            value.put("z", entity.posZ - this.getPos().getZ());
        }

        return value;
    }

    @SuppressWarnings({ "rawtypes" })
    public Map<Integer, HashMap<String, Object>> scan(boolean players, boolean offset) {
        Entity entity;
        Map<Integer, HashMap<String, Object>> output = new HashMap<>();
        int index = 1;
        List e = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).grow(range));
        if (!e.isEmpty()) {
            for (int i = 0; i <= e.size() - 1; i++) {
                entity = (Entity) e.get(i);
                if (players && entity instanceof EntityPlayerMP) {
                    output.put(index++, info(entity, offset));
                    //worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
                } else if (!players) {
                    output.put(index++, info(entity, offset));
                    //worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
                }
            }
        } else {
            //worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 3, 3);
        }
        return output;
    }

    @Callback
    public Object[] getLoc(Context context, Arguments args) {
        return new Object[] { this.getPos().getX(), this.getPos().getY(), this.getPos().getZ() };
    }

    @Callback(doc = "function(optional:int:range):table; pushes a signal \"entityDetect\" for each player in range, optional set range.", direct = true)
    public Object[] scanPlayers(Context context, Arguments args) {
        range = args.optInteger(0, range);
        offset = args.optBoolean(1, offset);
        if (range > OpenSecurity.rfidRange) {
            range = OpenSecurity.rfidRange;
        }
        range = range / 2;
        if (node.changeBuffer(-5 * range) == 0) {
        	world.playSound(null, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, SoundHandler.scanner1, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);
            return new Object[]{ scan(true, offset) };
        } else {
            return new Object[] { false, "Not enough power in OC Network." };
        }
    }

    @Callback(doc = "function(optional:int:range):table; pushes a signal \"entityDetect\" for each entity in range (excluding players), optional set range.", direct = true)
    public Object[] scanEntities(Context context, Arguments args) {
        range = args.optInteger(0, range);
        offset = args.optBoolean(1, offset);
        if (range > OpenSecurity.rfidRange) {
            range = OpenSecurity.rfidRange;
        }
        range = range / 2;
        if (node.changeBuffer(-5 * range) == 0) {
        	world.playSound(null, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, SoundHandler.scanner1, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);
            return new Object[]{ scan(false, offset) };
        } else {
            return new Object[] { false, "Not enough power in OC Network." };
        }
    }

}
