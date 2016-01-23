package pcl.opensecurity.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import pcl.opensecurity.OpenSecurity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntitySecureDoor extends TileEntity implements Environment{

	protected Node node = Network.newNode(this, Visibility.Network).create();
	
	String ownerUUID = "";
	String password = "";

	public TileEntitySecureDoor() {

	}

	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public void setPassword(String pass) {
		this.password = pass;
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			TileEntity te = worldObj.getTileEntity(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if (te instanceof TileEntitySecureDoor) {
				if (!te.equals(this)) {
					if (((TileEntitySecureDoor) te).getOwner().equals(this.ownerUUID)) {
						((TileEntitySecureDoor) te).setSlavePassword(this.password);	
					}
				}
			}
		}
	}

	public void setSlavePassword(String pass) {
		this.password = pass;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (node != null && node.host() == this) {
			node.load(tag.getCompoundTag("oc:node"));
		}
		
		this.ownerUUID = tag.getString("owner");
		this.password = tag.getString("password");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if (node != null && node.host() == this) {
			final NBTTagCompound nodeNbt = new NBTTagCompound();
			node.save(nodeNbt);
			tag.setTag("oc:node", nodeNbt);
		}
		
		tag.setString("owner", this.ownerUUID);
		tag.setString("password", this.password);
	}

	public String getOwner() {
		return this.ownerUUID;
	}

	public String getPass() {
		return this.password;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		
		if (worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor lowerDoor = (TileEntitySecureDoor) worldObj.getTileEntity(xCoord, yCoord - 1, zCoord);
			if (ownerUUID == null) {
				ownerUUID = lowerDoor.ownerUUID;
			}
		}
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

	@Override
	public void onConnect(final Node node) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnect(Node node) {
		// TODO Auto-generated method stub

	}
}
