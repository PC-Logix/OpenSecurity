package pcl.opensecurity.blocks;

import java.util.Random;

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
	public void updateTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
		world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityEntityDetector();
	}
	
}