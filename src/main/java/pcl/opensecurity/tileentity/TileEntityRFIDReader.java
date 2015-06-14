package pcl.opensecurity.tileentity;

import java.util.List;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Caitlyn
 *
 */
public class TileEntityRFIDReader extends TileEntityMachineBase implements Environment {

	public String data;
	public int range = 16;

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

	public boolean anyPlayerInRange()
	{
		return this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, 16D) != null;
	}


	@SuppressWarnings("rawtypes")
	public void scan() {
		data = null;
		if (anyPlayerInRange()) {
			List e = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord - range, this.yCoord - range, this.zCoord - range, this.xCoord + range, this.yCoord + range, this.zCoord + range));
			if (e.size() > 0) {
				for (int i = 0; i <= e.size() - 1; i++) {
					EntityPlayer em = (EntityPlayer) e.get(i);
					ItemStack[] playerInventory = em.inventory.mainInventory;
					int size = playerInventory.length;
					for(int k = 0; k < size; k++) {
						ItemStack st = em.inventory.getStackInSlot(k);
						if (st != null && st.getItem() instanceof ItemRFIDCard && st.stackTagCompound != null && st.stackTagCompound.hasKey("data")) {
							data = st.stackTagCompound.getString("data");
							double rangeToPlayer = em.getDistance(this.xCoord, this.yCoord, this.zCoord);
							node.sendToReachable("computer.signal", "rfidData", em.getDisplayName(), rangeToPlayer, data);
						}
					}
				}
			}  
		}
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
		scan();
		return new Object[] { "completed" };
	}
}