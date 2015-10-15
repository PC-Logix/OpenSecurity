package pcl.opensecurity.drivers;

import java.util.HashMap;
import java.util.List;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.EnvironmentHost;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.DriverItem;
import li.cil.oc.common.inventory.Inventory;
import li.cil.oc.common.item.TabletWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemRFIDCard;

public class RFIDReaderCardDriver extends DriverItem {

	public RFIDReaderCardDriver() {
		super(new ItemStack(ContentRegistry.rfidReaderCard));
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container) {
		if (container instanceof TileEntity)
			return new Environment(container);
		if (container instanceof TabletWrapper)
			return new Environment(container);
		return null;
	}

	@Override
	public String slot(ItemStack stack) {
		return Slot.Card;
	}

	public class Environment extends li.cil.oc.api.prefab.ManagedEnvironment {
		public String data = null;
		protected EnvironmentHost container = null;
		protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent("OSRFIDReader").withConnector(32).create();

		@Override
		public Node node() {
			return node;
		}

		public Environment(EnvironmentHost container3) {
			this.container = container3;
			this.setNode(node);
		}

		@Callback
		public Object[] greet(Context context, Arguments args) {
			return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
		}

		@Callback(doc = "function(optional:int:range):string; pushes a signal \"rfidData\" for each found rfid on all players in range, optional set range.", direct = true)
		public Object[] scan(Context context, Arguments args) throws Exception {
			double range = args.optDouble(0, OpenSecurity.rfidRange);
			if (range > OpenSecurity.rfidRange) {
				range = OpenSecurity.rfidRange;
			}
			range = range / 2;
			
			if (node.changeBuffer(-5 * range) == 0) {
				return new Object[]{ scan(range) };
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
				name = ((EntityPlayer) entity).getDisplayName();
			else
				name = entity.getCommandSenderName();
			node.sendToReachable("computer.signal", "rfidData", name, rangeToEntity, data, uuid);
			value.put("name", name);
			value.put("range", rangeToEntity);
			value.put("data", data);
			value.put("uuid", uuid);
			value.put("locked", locked);

			return value;
		}

		@SuppressWarnings({ "rawtypes" })
		public HashMap<Integer, HashMap<String, Object>> scan(double range) {
			Entity entity;
			HashMap<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
			int index = 1;
			List e = container.world().getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(container.xPosition() - range, container.yPosition() - range, container.zPosition() - range, container.xPosition() + range, container.yPosition() + range, container.zPosition() + range));
			if (!e.isEmpty()) {
				for (int i = 0; i <= e.size() - 1; i++) {
					entity = (Entity) e.get(i);
					if (entity instanceof EntityPlayerMP) {
						EntityPlayer em = (EntityPlayer) entity;
						ItemStack[] playerInventory = em.inventory.mainInventory;
						int size = playerInventory.length;
						for (int k = 0; k < size; k++) {
							ItemStack st = em.inventory.getStackInSlot(k);
							if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
								output.put(index++, info(entity, st.stackTagCompound.getString("data"), st.stackTagCompound.getString("uuid"), st.stackTagCompound.getBoolean("locked")));
							}
						}
					} else if (entity instanceof li.cil.oc.common.entity.Drone) {
						li.cil.oc.common.entity.Drone em = (li.cil.oc.common.entity.Drone) entity;
						Inventory droneInventory = em.mainInventory();
						int size = em.inventorySize();
						for (int k = 0; k < size; k++) {
							ItemStack st = droneInventory.getStackInSlot(k);
							if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
								output.put(index++, info(entity, st.stackTagCompound.getString("data"), st.stackTagCompound.getString("uuid"), st.stackTagCompound.getBoolean("locked")));
							}
						}
					}
					NBTTagCompound tag = entity.getEntityData().getCompoundTag("rfidData");
					if (tag.hasKey("data")) {
						output.put(index++, info(entity, tag.getString("data"), tag.getString("uuid"), tag.getBoolean("locked")));
					}
				}
			}
			return output;
		}
	}
}
