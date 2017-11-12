package pcl.opensecurity.common.tileentity;

import java.util.HashMap;
import java.util.List;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.common.entity.Drone;
import li.cil.oc.common.inventory.Inventory;
import net.minecraft.entity.Entity;
//import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.common.items.ItemRFIDCard;

public class TileEntityRFIDReader extends TileEntityOSBase {

	public String data;
	public String eventName = "magData";
	
	public TileEntityRFIDReader() {
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	private static String getComponentName() {
		return "os_rfidreader";
	}
	
	// Thanks gamax92 from #oc for the following 2 methods...
	private HashMap<String, Object> info(Entity entity, String data, String uuid, boolean locked) {
		HashMap<String, Object> value = new HashMap<String, Object>();

		double rangeToEntity = entity.getDistance(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ());
		String name;
		if (entity instanceof EntityPlayerMP)
			name = ((EntityPlayer) entity).getDisplayNameString();
		else
			name = entity.getName();
		node.sendToReachable("computer.signal", "rfidData", name, rangeToEntity, data, uuid);
		value.put("name", name);
		value.put("range", rangeToEntity);
		value.put("data", data);
		value.put("uuid", uuid);
		value.put("locked", locked);

		return value;
	}

	@SuppressWarnings({ "rawtypes" })
	public HashMap<Integer, HashMap<String, Object>> scan(int range) {
		boolean found = false;
		//world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3);
		//Block block = world.getBlock(this.xCoord, this.yCoord, this.zCoord);
		//world.scheduleBlockUpdate(this.xCoord, this.yCoord, this.zCoord, block, 20);
		Entity entity;
		HashMap<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
		int index = 1;
		List e = this.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1).expandXyz((double) range));
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
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.getTagCompound() != null && st.getTagCompound().hasKey("data")) {
							String localUUID;
							if (!OpenSecurity.ignoreUUIDs) {
								localUUID = st.getTagCompound().getString("uuid");
							} else {
								localUUID = "-1";
							}
							output.put(index++, info(entity, st.getTagCompound().getString("data"), localUUID, st.getTagCompound().getBoolean("locked")));
						}
					}
				} else if (entity instanceof li.cil.oc.common.entity.Drone) {
					found = true;
					Drone em = (Drone) entity;
					Inventory droneInventory = (Inventory) em.mainInventory();
					int size = em.inventorySize();
					for (int k = 0; k < size; k++) {
						ItemStack st = droneInventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.getTagCompound() != null && st.getTagCompound().hasKey("data")) {
							String localUUID;
							if (!OpenSecurity.ignoreUUIDs) {
								localUUID = st.getTagCompound().getString("uuid");
							} else {
								localUUID = "-1";
							}
							output.put(index++, info(entity, st.getTagCompound().getString("data"), localUUID, st.getTagCompound().getBoolean("locked")));
						}
					}
				}
				NBTTagCompound tag = entity.getEntityData().getCompoundTag("rfidData");
				if (tag.hasKey("data")) {
					found = true;
					String localUUID;
					if (!OpenSecurity.ignoreUUIDs) {
						localUUID = tag.getString("uuid");
					} else {
						localUUID = "-1";
					}
					output.put(index++, info(entity, tag.getString("data"), localUUID, tag.getBoolean("locked")));
				}
			}
		}

		//if (found) {
		//	world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 2, 3);
		//} else {
		//	world.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 3, 3);
		//}

		return output;
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Callback(doc = "function(optional:int:range):string; pushes a signal \"rfidData\" for each found rfid on all players in range, optional set range.")
	public Object[] scan(Context context, Arguments args) {
		int range = args.optInteger(0, OpenSecurity.rfidRange);
		if (range > OpenSecurity.rfidRange) {
			range = OpenSecurity.rfidRange;
		}

		range = range / 2;
		
		if (node.changeBuffer(-5 * range) == 0) {
        	world.playSound(null, this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, SoundHandler.scanner2, SoundCategory.BLOCKS, 15 / 15 + 0.5F, 1.0F);
			return new Object[]{ scan(range) };
		} else {
			return new Object[] { false, "Not enough power in OC Network." };
		}
	}
}
