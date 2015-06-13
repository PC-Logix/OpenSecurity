package pcl.opensecurity.blocks;

import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockRFIDReader extends BlockContainer {

	public BlockRFIDReader() {
		super(Material.iron);
		setBlockName("rfidreader");
		setBlockTextureName("opensecurity:rfidreader");
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityRFIDReader();
	}
}
