package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.common.tileentity.logic.DoorController;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.interfaces.IOwner;

import java.util.UUID;

public class TileEntityDoorController extends TileEntityOSCamoBase implements IOwner, IProtection {
	final static String NAME = "os_doorcontroller";
	private UUID ownerUUID;

	public TileEntityDoorController(){
		super(NAME);
		node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();
	}

	public TileEntityDoorController(EnvironmentHost host){
		super(NAME, host);
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
		if(!action.equals(Protection.UserAction.explode) && ownerUUID != null && entityIn.getUniqueID().equals(ownerUUID))
			return false;

		if(entityIn != null && entityIn instanceof EntityPlayer)
			((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this block is protected"), false);

		return true;
	}

	// OC Callbacks

	@Callback
	public Object[] isOpen(Context context, Arguments args) {
		return DoorController.isOpen(getWorld(), getPos());
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
		return DoorController.toggle(getWorld(), getPos(), args.optString(0, ""));
	}

	@Callback
	public Object[] open(Context context, Arguments args) {
		return DoorController.setDoorStates(getWorld(), getPos(), true, args.optString(0, ""));
	}

	@Callback
	public Object[] close(Context context, Arguments args) {
		return DoorController.setDoorStates(getWorld(), getPos(), false, args.optString(0, ""));
	}

	@Callback
	public Object[] removePassword(Context context, Arguments args) {
		return DoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), "");
	}

	@Callback
	public Object[] setPassword(Context context, Arguments args) {
		return DoorController.setDoorPasswords(getWorld(), getPos(), args.checkString(0), args.checkString(1));
	}

	// DoorControllerTile Methods
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
