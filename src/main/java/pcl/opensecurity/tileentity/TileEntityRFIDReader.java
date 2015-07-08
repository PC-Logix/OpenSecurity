package pcl.opensecurity.tileentity;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.common.inventory.Inventory;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemRFIDCard;

/**
 * @author Caitlyn
 *
 */
public class TileEntityRFIDReader extends TileEntityMachineBase implements Environment {

	public String data;
	public UUID uuid;
	public int range = OpenSecurity.rfidRange;

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

	private String getComponentName() {
		// TODO Auto-generated method stub
		return "os_rfidreader";
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
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
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
	private HashMap<String, Object> info(Entity entity, String data, String uuid) {
		HashMap<String, Object> value = new HashMap<String, Object>();

		double rangeToEntity = entity.getDistance(this.xCoord, this.yCoord, this.zCoord);
		String name;
		if (entity instanceof EntityPlayerMP)
			name = ((EntityPlayer) entity).getDisplayName();
		else
			name = entity.getCommandSenderName();
		node.sendToReachable("computer.signal", "rfidData", name, rangeToEntity, data, uuid);
		value.put("name", name);
		value.put("range", rangeToEntity);
		value.put("data", data);
		value.put("uuid", uuid);

		return value;
	}

	@SuppressWarnings({ "rawtypes" })
	public HashMap<Integer, HashMap<String, Object>> scan() {
		boolean found = false;
		worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
		Block block = worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
		worldObj.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, block, 20);
		Entity entity;
		HashMap<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
		int index = 1;
		List e = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord - range, this.yCoord - range, this.zCoord - range, this.xCoord + range, this.yCoord + range, this.zCoord + range));
		if (!e.isEmpty()) {
			for (int i = 0; i <= e.size() - 1; i++) {
				entity = (Entity) e.get(i);
				if (entity instanceof EntityPlayerMP) {
					found = true;
					EntityPlayer em = (EntityPlayer) entity;
					ItemStack[] playerInventory = em.inventory.mainInventory;
					int size = playerInventory.length;
					for (int k = 0; k < size; k++) {
						ItemStack st = em.inventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
							output.put(index++, info(entity, st.stackTagCompound.getString("data"), st.stackTagCompound.getString("uuid")));
						}
					}
				} else if (entity instanceof li.cil.oc.common.entity.Drone) {
					found = true;
					li.cil.oc.common.entity.Drone em = (li.cil.oc.common.entity.Drone) entity;
					Inventory droneInventory = em.mainInventory();
					int size = em.inventorySize();
					for (int k = 0; k < size; k++) {
						ItemStack st = droneInventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
							output.put(index++, info(entity, st.stackTagCompound.getString("data"), st.stackTagCompound.getString("uuid")));
						}
					}
				}
				NBTTagCompound tag = entity.getEntityData().getCompoundTag("rfidData");
				if (tag.hasKey("data")) {
					found = true;
					output.put(index++, info(entity, tag.getString("data"), tag.getString("uuid")));
				}
			}
		}

		if (found) {
			worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
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

	@Callback(doc = "function(optional:int:range):string; pushes a signal \"rfidData\" for each found rfid on all players in range, optional set range.", direct = true)
	public Object[] scan(Context context, Arguments args) {
		range = args.optInteger(0, range);
		if (range > OpenSecurity.rfidRange) {
			range = OpenSecurity.rfidRange;
		}
		range = range / 2;
		return new Object[] { scan() };
	}
}
