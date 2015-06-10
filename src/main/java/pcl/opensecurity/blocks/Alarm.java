/**
 * 
 */
package pcl.opensecurity.blocks;

import pcl.opensecurity.tileentity.AlarmTE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class Alarm extends BlockContainer {

	public Alarm() {
		super(Material.iron);
		setBlockName("alarm");
		// TODO Auto-generated constructor stub
	}

	private AlarmTE tile;
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new AlarmTE();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int xCoord, int yCoord, int zCoord, Block neighbourBlock) {
		boolean isRedstonePowered = world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		if (isRedstonePowered) {
			tile = (AlarmTE) world.getTileEntity(xCoord, yCoord, zCoord); //we make sure that tile is the TileEntitySpeaker thats at our blocks location (ok were not checking it is a TileEntitySpeaker, more casting it as...should be though unless something went wrong)
            tile.setShouldStart(true);
		} else {
			world.addBlockEvent(xCoord, yCoord, zCoord, this, 1, 0);
		}
	}
	
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPramater) {
        if (world.isRemote && eventId == 0) {
            tile = (AlarmTE) world.getTileEntity(x, y, z);
            tile.setShouldStart(true);
        }
        if (!world.isRemote && eventId == 0) {
            //do nothing
        }
        if (world.isRemote && eventId == 1) {
            tile = (AlarmTE) world.getTileEntity(x, y, z);
            tile.setShouldStop(true);
        }
        return true;
    }
}
