package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import pcl.opensecurity.common.blocks.BlockSecureDoor;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;

public class TileEntitySecureDoor extends TileEntity implements Environment, ITickable, IProtection {
	
	protected Node node = Network.newNode(this, Visibility.Network).create();
	
	String ownerUUID = "";
	String password = "";

	public TileEntitySecureDoor() {}

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
		if(!action.equals(Protection.UserAction.explode) && ownerUUID.length() > 0 && entityIn.getUniqueID().toString().equals(ownerUUID))
			return false;

		if(entityIn != null && entityIn instanceof EntityPlayer)
			((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this block is protected"), false);

		return true;
	}


	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public void setPassword(String pass) {
		this.password = pass;
		for (EnumFacing direction : EnumFacing.VALUES) {
			BlockPos neighbourPos = this.pos.offset(direction); // Offset the block's position by 1 block in the current direction
			IBlockState neighbourState = world.getBlockState(neighbourPos); // Get the IBlockState at the neighboring position
			Block neighbourBlock = neighbourState.getBlock(); // Get the IBlockState's Block
			if (neighbourBlock instanceof BlockSecureDoor){ // If the neighbouring block is a Door Block,
				TileEntity te = world.getTileEntity(neighbourPos);
				if (te instanceof TileEntitySecureDoor && !te.equals(this) && ((TileEntitySecureDoor) te).getOwner().equals(this.ownerUUID)) {
					((TileEntitySecureDoor) te).setSlavePassword(this.password);	
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

	@Override
	public void onConnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {		
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		
		if (world.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor lowerDoor = (TileEntitySecureDoor) world.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
			//System.out.println(lowerDoor.getPos());
			if (ownerUUID == null) {
				ownerUUID = lowerDoor.ownerUUID;
			}
			if (password.isEmpty()) {
				setPassword(lowerDoor.getPass());
			}
		}
		if (world.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) instanceof TileEntitySecureDoor) {
			TileEntitySecureDoor upperDoor = (TileEntitySecureDoor) world.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()));
			//System.out.println(upperDoor.getPos());
			if (ownerUUID == null) {
				ownerUUID = upperDoor.ownerUUID;
			}
			if (password.isEmpty()) {
				setPassword(upperDoor.getPass());
			}
		}
	}

	@Override
	public Node node() {
		return node;
	}

}
