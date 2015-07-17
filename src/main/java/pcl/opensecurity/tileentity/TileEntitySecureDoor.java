package pcl.opensecurity.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntitySecureDoor extends TileEntity {

	String ownerUUID = "";

	public TileEntitySecureDoor() {

	}

	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.ownerUUID = tag.getString("owner");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("owner", this.ownerUUID);
	}

	public String getOwner() {
		return this.ownerUUID;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor lowerDoor = (TileEntitySecureDoor) worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
			if (ownerUUID == null) {
				ownerUUID = lowerDoor.ownerUUID;
			}
		}
	}
}
