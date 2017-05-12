package pcl.opensecurity.tileentity;

import org.apache.commons.codec.binary.Base64;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityBiometricReader extends TileEntityMachineBase implements Environment {

	public String data;
	public String eventName = "bioReader";

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
		return "os_biometric";
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
		if (nbt.hasKey("eventName") && !nbt.getString("eventName").isEmpty()) {
			eventName = nbt.getString("eventName");
		} else {
			eventName = "bioReader";
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
		nbt.setString("eventName", eventName);
	}

	public void doRead(EntityPlayer entityplayer, int side) {
		if (!OpenSecurity.returnRealUUID) {
			byte[] bytesEncoded = Base64.encodeBase64(entityplayer.getUniqueID().toString().getBytes());
			node.sendToReachable("computer.signal", eventName, new String(bytesEncoded));
		} else {
			node.sendToReachable("computer.signal", eventName, ntityplayer.getUniqueID().toString());
		}
		worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D,  this.zCoord + 0.5D, "opensecurity:scanner3", 0.4F, 1);
	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent", direct = true)
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
	}
	
}
