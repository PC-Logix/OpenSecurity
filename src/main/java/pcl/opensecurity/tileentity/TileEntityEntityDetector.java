package pcl.opensecurity.tileentity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;

/**
 * @author Caitlyn
 *
 */
public class TileEntityEntityDetector extends TileEntityMachineBase implements Environment {

	public int range = OpenSecurity.rfidRange;
	public boolean offset = false;

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	@Override
	public Node node() {
		return node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null)
			node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null)
			node.remove();
	}

	private static String getComponentName() {
		// TODO Auto-generated method stub
		return "os_entdetector";
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final Node node) {

	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
	}

	// Thanks gamax92 from #oc for the following 2 methods...
	private HashMap<String, Object> info(Entity entity, boolean offset) {
		HashMap<String, Object> value = new HashMap<String, Object>();

		double rangeToEntity = entity.getDistance(this.xCoord, this.yCoord, this.zCoord);
		String name;
		if (entity instanceof EntityPlayerMP)
			name = ((EntityPlayer) entity).getDisplayName();
		else
			name = entity.getCommandSenderName();
		
		value.put("name", name);
		value.put("range", rangeToEntity);
		if (!offset) {
			node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity, entity.posX, entity.posY, entity.posZ);
			value.put("x", entity.posX);
			value.put("y", entity.posY);
			value.put("z", entity.posZ);
		} else {
			node.sendToReachable("computer.signal", "entityDetect", name, rangeToEntity, entity.posX - this.xCoord, entity.posY - this.yCoord, entity.posZ - this.zCoord);
			value.put("x", entity.posX - this.xCoord);
			value.put("y", entity.posY - this.yCoord);
			value.put("z", entity.posZ - this.zCoord);
		}

		return value;
	}

	@SuppressWarnings({ "rawtypes" })
	public Map<Integer, HashMap<String, Object>> scan(boolean players, boolean offset) {
		worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
		Block block = worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
		worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, block, 20);
		Entity entity;
		Map<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
		int index = 1;
		List e = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord - range, this.yCoord - range, this.zCoord - range, this.xCoord + range, this.yCoord + range, this.zCoord + range));
		if (!e.isEmpty()) {
			for (int i = 0; i <= e.size() - 1; i++) {
				entity = (Entity) e.get(i);
				if (players && entity instanceof EntityPlayerMP) {
					output.put(index++, info(entity, offset));
					worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
				} else if (!players) {
					output.put(index++, info(entity, offset));
					worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
				}
			}
		} else {
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 3, 3);
		}
		return output;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}
	
	@Callback
	public Object[] getLoc(Context context, Arguments args) {
		return new Object[] { this.xCoord, this.yCoord, this.zCoord };
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
			//worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D,  this.zCoord + 0.5D, "opensecurity:scanner1", 1.0F, 1);
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
			//worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D,  this.zCoord + 0.5D, "opensecurity:scanner1", 1.0F, 1);
			//System.out.println(range * 0.1F + 0.9F);
			return new Object[]{ scan(false, offset) };
		} else {
			return new Object[] { false, "Not enough power in OC Network." };
		}
	}

}
