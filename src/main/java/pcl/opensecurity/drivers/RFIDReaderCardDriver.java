package pcl.opensecurity.drivers;

import java.util.List;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemRFIDCard;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
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
import li.cil.oc.common.item.TabletWrapper;

public class RFIDReaderCardDriver extends DriverItem {

	public RFIDReaderCardDriver() {
		super(new ItemStack(OpenSecurity.rfidReaderCard));
	}

	@Override
	public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost container)
	{
		if (container instanceof TileEntity)
			return new Environment(container);
		if (container instanceof TabletWrapper)
			return new Environment(container);
		return null;
	}

	@Override
	public String slot(ItemStack stack)
	{
		return Slot.Card;
	}

	public class Environment extends li.cil.oc.api.prefab.ManagedEnvironment {
		public double range = OpenSecurity.rfidRange;
		public String data = null;
		protected EnvironmentHost container = null;
		protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent("OSRFIDReader").withConnector(32).create();
		@Override
		public Node node() {
			return (Node) node;
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
		public Object[] scan(Context context, Arguments args) {
			range = args.optDouble(0, range);
			if (range > OpenSecurity.rfidRange) {
				range = OpenSecurity.rfidRange;
			}
			scan();
			return new Object[] { "completed" };
		}
/*
		@Callback
		public Object[] write(Context context, Arguments args)
		{
			if (container instanceof TabletWrapper) {
				EntityPlayer entityplayer = container.world().getClosestPlayer(container.xPosition(), container.yPosition(), container.zPosition(), 1.63D);
				Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
				if (!container.world().isRemote) {
					if (equipped != null && equipped instanceof Tablet) {
						
					}
				}
			}
			return new Object[] { "completed" };
		}
*/
		@SuppressWarnings("rawtypes")
		public void scan() {
			data = null;
			List e = container.world().getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(container.xPosition() - range, container.yPosition() - range, container.zPosition() - range, container.xPosition() + range, container.yPosition() + range, container.zPosition() + range));
			if (e.size() > 0) {
				for (int i = 0; i <= e.size() - 1; i++) {
					EntityPlayer em = (EntityPlayer) e.get(i);
					ItemStack[] playerInventory = em.inventory.mainInventory;
					int size = playerInventory.length;
					for(int k = 0; k < size; k++) {
						ItemStack st = em.inventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
							data = st.stackTagCompound.getString("data");
							System.out.println(data);
							double rangeToPlayer = em.getDistance(container.xPosition(), container.yPosition(), container.zPosition());
							node.sendToReachable("computer.signal", "rfidData", em.getDisplayName(), rangeToPlayer, data);
						}
					}
				}
			}  
		}
	}
}
