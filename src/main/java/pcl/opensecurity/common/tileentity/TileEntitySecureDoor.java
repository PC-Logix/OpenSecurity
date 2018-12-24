package pcl.opensecurity.common.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import li.cil.repack.org.luaj.vm2.ast.Str;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.protection.IProtection;
import pcl.opensecurity.common.protection.Protection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.logging.Logger;

import static net.minecraft.block.BlockDoor.HALF;

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
		if(!action.equals(Protection.UserAction.explode) && entityIn.getUniqueID().toString().equals(ownerUUID))
			return false;

		if(entityIn != null && entityIn instanceof EntityPlayer)
			((EntityPlayer) entityIn).sendStatusMessage(new TextComponentString("this door is protected"), true);

		return true;
	}

	public ArrayList<TileEntitySecureDoor> getDoorTiles(){
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
	}

	@Override
	public Node node() {
		return node;
	}


	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, writeToNBT(new NBTTagCompound()));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}


	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}


	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

}
