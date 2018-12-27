package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;

import java.util.ArrayList;
import java.util.logging.Logger;

import static net.minecraft.block.BlockDoor.HALF;

public class TileEntitySecureDoor extends TileEntityOSBase implements IProtection {
	private String ownerUUID = "";
	private String password = "";

	public TileEntitySecureDoor() {
		super("os_securedoor");
		//node = Network.newNode(this, Visibility.Network).create();
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
		if(!action.equals(Protection.UserAction.explode) && entityIn.getUniqueID().toString().equals(ownerUUID))
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


	public void setOwner(String UUID) {
		for(TileEntitySecureDoor door : getDoorTiles())
			door.ownerUUID = UUID;
	}

	public void setPassword(String pass) {
		for(TileEntitySecureDoor door : getDoorTiles())
			door.password = pass;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		this.ownerUUID = tag.getString("owner");
		this.password = tag.getString("password");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setString("owner", this.ownerUUID);
		tag.setString("password", this.password);
		return tag;
	}

	public String getOwner() {
		return this.ownerUUID;
	}

	public String getPass() {
		return this.password;
	}

}
