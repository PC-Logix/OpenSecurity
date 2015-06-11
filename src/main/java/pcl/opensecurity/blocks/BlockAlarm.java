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
	public void onNeighborBlockChange(World world, int xCoord, int yCoord, int zCoord, Block neighbourBlock) {
		boolean isRedstonePowered = world.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		tile = (TileEntityAlarm) world.getTileEntity(xCoord, yCoord, zCoord);
		if (isRedstonePowered) {
			tile.setShouldStart(true);
		} else {
			tile.setShouldStop(true);
		}
	}
}
