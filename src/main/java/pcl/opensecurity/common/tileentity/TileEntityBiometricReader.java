package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import pcl.opensecurity.common.SoundHandler;
import pcl.opensecurity.common.inventory.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityBiometricReader extends TileEntityOSBase {

	public String data;
	public String eventName = "bioReader";

	public TileEntityBiometricReader() {
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}
	
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
		return "os_biometric";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (node != null && node.host() == this) {
			node.load(nbt.getCompoundTag("oc:node"));
		}
		if (nbt.hasKey("eventName") && !nbt.getString("eventName").isEmpty()) {
			eventName = nbt.getString("eventName");
		} else {
			eventName = "bioReader";
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			nbt.setTag("oc:node", nodeNbt);
		}
		nbt.setString("eventName", eventName);
		return nbt;
	}

	public void doRead(EntityPlayer entityplayer, EnumFacing side) {
		node.sendToReachable("computer.signal", eventName, entityplayer.getUniqueID().toString());
		world.playSound(null, this.pos, SoundHandler.scanner3, SoundCategory.BLOCKS, 0.4F, 1);
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent", direct = true)
	public Object[] setEventName(Context context, Arguments args) {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
}
