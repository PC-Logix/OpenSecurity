package pcl.opensecurity.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntitySecureDoor extends TileEntityMachineBase {

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
}
