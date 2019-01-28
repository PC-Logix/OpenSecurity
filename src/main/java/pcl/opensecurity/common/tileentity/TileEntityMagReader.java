package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.items.ItemMagCard;

import javax.annotation.Nonnull;

//import net.minecraft.client.audio.SoundCategory;

public class TileEntityMagReader extends TileEntityOSBase {
	public String data;
	private String eventName = "magData";
	
	public TileEntityMagReader() {
		super("os_magreader");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public boolean doRead(@Nonnull ItemStack itemStack, EntityPlayer em, int side) {
		if (itemStack.getItem() instanceof ItemMagCard && itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey("data")) {
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
				if (!Config.getConfig().getCategory("general").get("ignoreUUIDs").getBoolean()) {
					user = em.getDisplayNameString();
				} else {
					user = "player";
				}
				node.sendToReachable("computer.signal", eventName, user, data, localUUID, locked, side);
			}
			getUpdateTag();
			world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
			markDirty();
			return true;
		} else {
			getUpdateTag();
			world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
			markDirty();
			return false;
		}
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
}
