package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.items.ItemCard;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.interfaces.IOwner;
import pcl.opensecurity.common.interfaces.IPasswordProtected;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import static net.minecraft.block.BlockDoor.HALF;

public class TileEntitySecureDoor extends TileEntityOSBase implements IProtection, IPasswordProtected, IOwner {
	private UUID ownerUUID;
	private String password = "";
	private String eventName = "magData";
	private Boolean hasMagReader = false;

	public TileEntitySecureDoor() {
		super("os_magreader");
		if (hasMagReader) {
			node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
		} else {
			node = Network.newNode(this, Visibility.None).withComponent(getComponentName()).withConnector(32).create();
		}
	}

	@Override
	public void validate(){
		super.validate();
		Protection.addArea(getWorld(), new AxisAlignedBB(getPos()), getPos());
	}

	@Override
	public void invalidate() {
		Protection.removeArea(getWorld(), getPos());
		super.invalidate();
	}

	@Override
	public boolean isProtected(Entity entityIn, Protection.UserAction action){
		if(!action.equals(Protection.UserAction.explode) && entityIn.getUniqueID().equals(ownerUUID))
			return false;

		if(entityIn != null && entityIn instanceof EntityPlayer)
			((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this door is protected"), true);

		return true;
	}

	private ArrayList<TileEntitySecureDoor> getDoorTiles(){
		ArrayList<TileEntitySecureDoor> doorTEs = new ArrayList<>();
		doorTEs.add(this);

		int offset = getWorld().getBlockState(getPos()).getValue(HALF).equals(BlockDoor.EnumDoorHalf.UPPER) ? -1 : 1;

		TileEntity teDoorOtherPart = world.getTileEntity(getPos().add(0, offset, 0));
		if(teDoorOtherPart instanceof TileEntitySecureDoor)
			doorTEs.add((TileEntitySecureDoor) teDoorOtherPart);
		else
			Logger.getLogger(OpenSecurity.MODID).warning("failed to get all door Tiles");

		return doorTEs;
	}

	public boolean doRead(@Nonnull ItemStack itemStack, EntityPlayer em, EnumFacing side) {
		if (!hasMagReader)
			return false;

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


	public void setOwner(UUID uuid) {
		for(TileEntitySecureDoor door : getDoorTiles())
			door.ownerUUID = uuid;
	}

	public void setPassword(String pass) {
		for(TileEntitySecureDoor door : getDoorTiles())
			door.password = pass;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if(tag.hasUniqueId("owner"))
			this.ownerUUID = tag.getUniqueId("owner");
		else if(tag.hasKey("owner")) //keep this for compat with old nbt tags in world (after first worldsave they are "fixed"
			this.ownerUUID = UUID.fromString(tag.getString("owner"));
		else
			this.ownerUUID = null;

		this.password = tag.getString("password");
		if(tag.hasKey("hasMag"))
			this.hasMagReader = tag.getBoolean("hasMag");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if(ownerUUID != null)
			tag.setUniqueId("owner", this.ownerUUID);

		tag.setString("password", this.password);
		tag.setBoolean("hasMag", this.hasMagReader);
		return tag;
	}

	public UUID getOwner() {
		return this.ownerUUID;
	}

	public String getPass() {
		return this.password;
	}

	public void enableMagReader() {
		System.out.println("Setting magreader true");
		hasMagReader = true;
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}
}
