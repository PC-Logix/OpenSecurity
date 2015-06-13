package pcl.opensecurity.blocks;

import pcl.opensecurity.tileentity.TileEntityAlarm;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockAlarm extends BlockContainer {

	public BlockAlarm() {
		super(Material.iron);
		setBlockName("alarm");
	}

	private TileEntityAlarm tile;

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

	@Override
	public void onNeighborBlockChange(World world, int xCoord, int yCoord,
			int zCoord, Block neighbourBlock) {
		boolean isRedstonePowered = world.isBlockIndirectlyGettingPowered(
				xCoord, yCoord, zCoord);
		tile = (TileEntityAlarm) world.getTileEntity(xCoord, yCoord, zCoord);
		if (isRedstonePowered) {
			world.addBlockEvent(xCoord, yCoord, zCoord, this, 0, 0);
		} else {
			world.addBlockEvent(xCoord, yCoord, zCoord, this, 1, 0);
		}
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z,
			int eventId, int eventPramater) {
		tile = (TileEntityAlarm) world.getTileEntity(x, y, z);
		if (eventId == 0) {
			tile.setShouldStart(true);
		}
		if (world.isRemote && eventId == 1) {
			tile.setShouldStop(true);
		}
		return true;
	}
}
