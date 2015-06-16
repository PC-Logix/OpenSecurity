package pcl.opensecurity.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.tileentity.TileEntityEntityDetector;

public class BlockEntityDetector extends BlockContainer {

	public BlockEntityDetector() {
		super(Material.iron);
		setBlockName("entitydetector");
		setBlockTextureName("opensecurity:entitydetector");
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityEntityDetector();
	}
	
}