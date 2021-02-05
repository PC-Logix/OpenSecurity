package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.Config;
import pcl.opensecurity.common.interfaces.IOwner;
import pcl.opensecurity.common.items.ItemCard;

import javax.annotation.Nonnull;
import java.util.UUID;

//import net.minecraft.client.audio.SoundCategory;

public class TileEntityMagReader extends TileEntityOSCamoBase implements IOwner {
	public String data;
	private String eventName = "magData";
	private UUID ownerUUID;
	
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


	@Override //IOwner
	public void setOwner(UUID uuid) {
		this.ownerUUID = uuid;
	}

	@Override //IOwner
	public UUID getOwner() {
		return this.ownerUUID;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(nbt.hasUniqueId("owner"))
			this.ownerUUID = nbt.getUniqueId("owner");
		else if(nbt.hasKey("owner")) //keep this for compat with old nbt tags in world (after first worldsave they are "fixed"
			this.ownerUUID = UUID.fromString(nbt.getString("owner"));
		else
			this.ownerUUID = null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(ownerUUID != null)
			nbt.setUniqueId("owner", this.ownerUUID);

		return nbt;
	}

	@Override
	public boolean playerCanChangeCamo(EntityPlayer player){
		return player.isCreative() || getOwner() == null || getOwner().equals(player.getUniqueID());
	}
}
