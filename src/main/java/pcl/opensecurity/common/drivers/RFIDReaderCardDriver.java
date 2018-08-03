package pcl.opensecurity.common.drivers;

import java.util.HashMap;
import java.util.List;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverItem;
import li.cil.oc.common.inventory.Inventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.items.ItemRFIDCard;

public class RFIDReaderCardDriver extends DriverItem {

	public RFIDReaderCardDriver() {
		super(new ItemStack(ContentRegistry.rfidReaderCardItem));
	}
	
	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container)
	{
		if (container.world() != null && container.world().isRemote)
			return null;
		return new Environment(container);
	}

	@Override
	public String slot(ItemStack stack) {
		// TODO Auto-generated method stub
		return Slot.Card;
	}
	
	public class Environment extends li.cil.oc.api.prefab.AbstractManagedEnvironment {
		public String data = null;
		protected li.cil.oc.api.network.EnvironmentHost container = null;
		protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent("os_rfidreader").withConnector(32).create();

		@Override
		public Node node() {
			return node;
		}

		public Environment(li.cil.oc.api.network.EnvironmentHost arg1) {
			this.container = arg1;
			this.setNode(node);
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { "Lasciate ogne speranza, voi ch'entrate" };
		}

		@Callback(doc = "function(optional:int:range):string; pushes a signal \"rfidData\" for each found rfid on all players in range, optional set range.")
		public Object[] scan(Context context, Arguments args) throws Exception {
			double range = args.optDouble(0, OpenSecurity.rfidRange);
			if (range > OpenSecurity.rfidRange) {
				range = OpenSecurity.rfidRange;
			}
			range = range / 2;
			
			if (node.changeBuffer(-5 * range) == 0) {
				return new Object[]{ scan((int) range) };
			} else {
				throw new Exception("Not enough power in OC Network.");
			}
		}

		// Thanks gamax92 from #oc for the following 2 methods...
		private HashMap<String, Object> info(Entity entity, String data, String uuid, boolean locked) {
			HashMap<String, Object> value = new HashMap<String, Object>();

			double rangeToEntity = entity.getDistance(container.xPosition(), container.yPosition(), container.zPosition());
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
			List e = container.world().getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(container.xPosition(), container.yPosition(), container.zPosition(), container.xPosition(), container.yPosition(), container.zPosition()).grow(range, range, range));
			if (!e.isEmpty()) {
				for (int i = 0; i <= e.size() - 1; i++) {
					entity = (Entity) e.get(i);
					if (entity instanceof EntityPlayerMP) {
						found = true;
						EntityPlayer em = (EntityPlayer) entity;
						NonNullList<ItemStack> playerInventory = em.inventory.mainInventory;
						int size = playerInventory.size();
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
						li.cil.oc.common.entity.Drone em = (li.cil.oc.common.entity.Drone) entity;
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
			return output;
		}
	}
}