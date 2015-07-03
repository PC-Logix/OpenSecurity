package pcl.opensecurity.tileentity;

import pcl.opensecurity.blocks.BlockSecurityDoor;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Caitlyn
 *
 */
public class TileEntityDoorController extends TileEntityMachineBase implements Environment {

	public Block block = null;

	private BlockSecurityDoor door = null;
	
	int doorCoordX;
	int doorCoordY;
	int doorCoordZ;
	
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
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		node.save(par1NBTTagCompound);
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

	@Callback
	public Object[] toggle(Context context, Arguments args) {
        int i1 = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);
        int j2;
        
        if ((i1 & 8) == 0)
        {
            int doorBottomMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY, doorCoordZ);
            j2 = doorBottomMeta & 7;
            j2 ^= 4;
    		worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY, doorCoordZ, j2, 2);
    		worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY, doorCoordZ, doorCoordX, doorCoordY, doorCoordZ);
        }
        else
        {
            int doorTopMeta = worldObj.getBlockMetadata(doorCoordX, doorCoordY - 1, doorCoordZ);
            j2 = doorTopMeta & 7;
            j2 ^= 4;
    		worldObj.setBlockMetadataWithNotify(doorCoordX, doorCoordY - 1, doorCoordZ, j2, 2);
    		worldObj.markBlockRangeForRenderUpdate(doorCoordX, doorCoordY - 1, doorCoordZ, doorCoordX, doorCoordY - 1, doorCoordZ);
        }
        boolean status;
        if (j2 == 4) {
        	status = true;
        } else {
        	status = false;
        }
        
		return new Object[] { status };
	}
}
