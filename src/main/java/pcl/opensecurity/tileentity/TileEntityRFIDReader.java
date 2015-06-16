package pcl.opensecurity.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemRFIDCard;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

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
		return (Node) node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null) node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null) node.remove();
	}

	private String getComponentName() {
		// TODO Auto-generated method stub
		return "OSRFIDReader";
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final Node node) {
		node.remove();
	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		node.load(par1NBTTagCompound);
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		node.save(par1NBTTagCompound);
	}

	//Thanks gamax92 from #oc for the following 2 methods...
	private HashMap<String, Object> info(Entity entity, String data, String uuid)
	{
		HashMap<String, Object> value = new HashMap<String, Object>();

		double rangeToEntity = entity.getDistance(this.xCoord, this.yCoord, this.zCoord);
		String name;
		if (entity instanceof EntityPlayerMP)
			name = ((EntityPlayer) entity).getDisplayName();
		else
			name = entity.getCommandSenderName();
		node.sendToReachable("computer.signal", "rfidData", name, rangeToEntity, data, uuid);
		value.put("name", name);
		value.put("range", (Double)rangeToEntity);
		value.put("data", data);
		value.put("uuid", uuid); // I don't know how UUID's work

		return value;
	}

	@SuppressWarnings({ "rawtypes" })
	public HashMap<Integer, HashMap<String, Object>> scan() {
		Entity entity;
		HashMap<Integer, HashMap<String, Object>> output = new HashMap<Integer, HashMap<String, Object>>();
		int index = 1;
		List e = this.worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(this.xCoord - range, this.yCoord - range, this.zCoord - range, this.xCoord + range, this.yCoord + range, this.zCoord + range));
		if (!e.isEmpty()) {
			for (int i = 0; i <= e.size() - 1; i++) {
				entity = (Entity) e.get(i);
				if (entity instanceof EntityPlayerMP) {
					EntityPlayer em = (EntityPlayer) entity;
					ItemStack[] playerInventory = em.inventory.mainInventory;
					int size = playerInventory.length;
					for(int k = 0; k < size; k++) {
						ItemStack st = em.inventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
							output.put(index++, info(entity, st.stackTagCompound.getString("data"), st.stackTagCompound.getString("uuid")));
						}
					}
				}
				NBTTagCompound tag = entity.getEntityData().getCompoundTag("rfidData");
				if(tag.hasKey("data")) {
					output.put(index++, info(entity, tag.getString("data"), tag.getString("uuid")));
				}
			}
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