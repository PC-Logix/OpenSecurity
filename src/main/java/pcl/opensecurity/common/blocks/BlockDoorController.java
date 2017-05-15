package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityDataBlock;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;

public class BlockDoorController extends BlockOSBase {

	public BlockDoorController(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("door_controller");
		setRegistryName("door_controller");
		setHardness(.5f);
		random = new Random();
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityDoorController();
	}
	
}
