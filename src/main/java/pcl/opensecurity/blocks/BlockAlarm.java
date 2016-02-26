package pcl.opensecurity.blocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.tileentity.TileEntityAlarm;

/**
 * @author Caitlyn
 *
 */
public class BlockAlarm extends BlockOSBase {

	public BlockAlarm() {
		setBlockName("alarm");
		setBlockTextureName("opensecurity:alarm");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

	@Override
	public void onNeighborBlockChange(World world, int xCoord, int yCoord, int zCoord, Block neighbourBlock) {
		boolean isRedstonePowered = world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		if (isRedstonePowered) {
			// world.addBlockEvent(xCoord, yCoord, zCoord, this, 0, 0);
		} else {
			// world.addBlockEvent(xCoord, yCoord, zCoord, this, 1, 0);
		}
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPramater) {
		TileEntityAlarm tile = (TileEntityAlarm) world.getTileEntity(x, y, z);
		if (eventId == 0 && !tile.computerPlaying) {
			tile.setShouldStart(true);
		}
		if (world.isRemote && eventId == 1 && !tile.computerPlaying) {
			tile.setShouldStop(true);
		}
		return true;
	}
}
