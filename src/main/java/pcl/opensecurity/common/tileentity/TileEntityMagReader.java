package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
//import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.items.ItemMagCard;

public class TileEntityMagReader extends TileEntityMachineBase {

	public String data;
	public String eventName = "magData";
	
	public TileEntityMagReader() {
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
		return "os_magreader";
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

	public boolean doRead(ItemStack itemStack, EntityPlayer em, int side) {
		if (itemStack != null && itemStack.getItem() instanceof ItemMagCard /*&& this.blockMetadata == 0*/) {
			if(!worldObj.isRemote){
				//worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D,  this.zCoord + 0.5D, "opensecurity:card_swipe", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
		if (itemStack != null && itemStack.getItem() instanceof ItemMagCard && itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("data")) {
			data = itemStack.getTagCompound().getString("data");
			String uuid = itemStack.getTagCompound().getString("uuid");
			String user;
			boolean locked = itemStack.getTagCompound().getBoolean("locked");
			if (node.changeBuffer(-5) == 0) {
				String localUUID;
				if (!OpenSecurity.ignoreUUIDs) {
					localUUID = uuid;
				} else {
					localUUID = "-1";
				}
				if (OpenSecurity.cfg.magCardDisplayName) {
					user = em.getDisplayNameString();
				} else {
					user = "player";
				}
				node.sendToReachable("computer.signal", eventName, user, data, localUUID, locked, side);
			}
			return true;
		} else {
			return false;
		}
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) throws Exception {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
}
