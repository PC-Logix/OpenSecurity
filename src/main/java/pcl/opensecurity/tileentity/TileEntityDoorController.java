package pcl.opensecurity.tileentity;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blocks.BlockSecurityDoor;
import pcl.opensecurity.util.BlockLocation;
import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ComponentConnector;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Caitlyn
 *
 */
public class TileEntityDoorController extends TileEntityMachineBase implements Environment {

	public Block block = null;

	public BlockSecurityDoor door;

	int doorCoordX;
	int doorCoordY;
	int doorCoordZ;

	String ownerUUID = "";

	public TileEntityDoorController() {

	}

	protected ComponentConnector node = Network.newNode(this, Visibility.Network).withComponent(getComponentName()).withConnector(32).create();

	@Override
	public Node node() {
		return (Node) node;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if (node != null) node.remove();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (node != null) node.remove();
	}

	private String getComponentName() {
		// TODO Auto-generated method stub
		return "os_door";
	}

	@Override
	public void onConnect(Node arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnect(final Node node) {

	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		node.load(par1NBTTagCompound);
		this.ownerUUID = par1NBTTagCompound.getString("owner");
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		node.save(par1NBTTagCompound);
		par1NBTTagCompound.setString("owner", this.ownerUUID);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (node != null && node.network() == null) {
			Network.joinOrCreateNetwork(this);
		}
		for(ForgeDirection direction: ForgeDirection.VALID_DIRECTIONS){
			//if (!(this.door instanceof BlockSecurityDoor)) {
			block = worldObj.getBlock(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ);
			if(block instanceof BlockSecurityDoor){
				this.door = (BlockSecurityDoor) block;
				doorCoordX = xCoord + direction.offsetX;
				doorCoordY = yCoord + direction.offsetY;
				doorCoordZ = zCoord + direction.offsetZ;
			}
			//}
		}

	}

	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}


	private int getDoorOrientation(BlockDoor door, BlockLocation loc) {
		return door.func_150013_e(loc.blockAccess, loc.x, loc.y, loc.z);
	}
	private boolean isDoorOpen(BlockDoor door, BlockLocation loc) {
		return door.func_150015_f(loc.blockAccess, loc.x, loc.y, loc.z);
	}
	private boolean isDoorMirrored(BlockDoor door, BlockLocation loc) {
		return ((door.func_150012_g(loc.blockAccess, loc.x, loc.y, loc.z) & 16) != 0);
	}

	@Callback
	public Object[] isOpen(Context context, Arguments args) {
		BlockSecurityDoor door = (BlockSecurityDoor)OpenSecurity.SecurityDoor;
		BlockLocation loc = BlockLocation.get(worldObj, doorCoordX, doorCoordY, doorCoordZ);
		return new Object[] { isDoorOpen(door, loc) };
	}

	@Callback
	public Object[] toggle(Context context, Arguments args) {
		BlockSecurityDoor door = (BlockSecurityDoor)OpenSecurity.SecurityDoor;
		BlockLocation loc = BlockLocation.get(worldObj, doorCoordX, doorCoordY, doorCoordZ);

		int direction = getDoorOrientation(door, loc);
		//boolean isOpen = isDoorOpen(door, loc);
		boolean isMirrored = isDoorMirrored(door, loc);

		int i = (isMirrored ? -1 : 1);
		switch (direction) {
		case 0: loc = loc.relative(0, 0,  i); break;
		case 1: loc = loc.relative(-i, 0, 0); break;
		case 2: loc = loc.relative(0, 0, -i); break;
		case 3: loc = loc.relative( i, 0, 0); break;
		}

		if ((loc.getBlock() == door) && (getDoorOrientation(door, loc) == direction) && (isDoorMirrored(door, loc) != isMirrored)) {


			int i1 = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);
			int j2;

			if ((i1 & 8) == 0)
			{
				int doorBottomMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);
				j2 = doorBottomMeta & 7;
				j2 ^= 4;
				worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY, doorCoordZ, j2, 2);
				worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY, doorCoordZ, doorCoordX, doorCoordY, doorCoordZ);
				worldObj.setBlockMetadataWithNotify(loc.x, loc.y, loc.z, j2, 2);
				worldObj.markBlockRangeForRenderUpdate(loc.x, loc.y, loc.z, loc.x, loc.y, loc.z);
			}
			else
			{
				int doorBottomMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY - 1, doorCoordZ);
				j2 = doorBottomMeta & 7;
				j2 ^= 4;
				worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY - 1, doorCoordZ, j2, 2);
				worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY - 1, doorCoordZ, doorCoordX, doorCoordY - 1, doorCoordZ);
				worldObj.setBlockMetadataWithNotify(loc.x, loc.y - 1, loc.z, j2, 2);
				worldObj.markBlockRangeForRenderUpdate(loc.x, loc.y, loc.z - 1, loc.x, loc.y, loc.z);
			}
		}


		return new Object[] { !isDoorOpen(door, loc) };
	}



	public void setOwner(String UUID) {
		this.ownerUUID = UUID;
	}

	public String getOwner() {
		return this.ownerUUID;
	}
}
