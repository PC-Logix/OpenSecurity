package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import pcl.opensecurity.Config;
import pcl.opensecurity.common.items.ItemCard;

import javax.annotation.Nonnull;

//import net.minecraft.client.audio.SoundCategory;

public class TileEntityMagReader extends TileEntityOSBase {
	public String data;
	private String eventName = "magData";
	
	public TileEntityMagReader() {
		super("os_magreader");
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public boolean doRead(@Nonnull ItemStack itemStack, EntityPlayer em, EnumFacing side) {
		ItemCard.CardTag cardTag = new ItemCard.CardTag(itemStack);

		if (node.changeBuffer(-5) != 0 || !cardTag.isValid)
			return false;

		String user = Config.getConfig().getCategory("general").get("ignoreUUIDs").getBoolean() ? "player" : em.getDisplayNameString();
		node.sendToReachable("computer.signal", eventName, user, cardTag.dataTag, cardTag.localUUID, cardTag.locked, side.getIndex());
		return true;
	}

	@Callback(doc = "function(String:name):boolean; Sets the name of the event that gets sent when a card is swipped", direct = true)
	public Object[] setEventName(Context context, Arguments args) {
		eventName = args.checkString(0);
		return new Object[]{ true };
	}
}
