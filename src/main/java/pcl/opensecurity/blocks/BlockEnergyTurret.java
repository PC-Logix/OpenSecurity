package pcl.opensecurity.blocks;

import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockEnergyTurret extends BlockOSBase {

	public BlockEnergyTurret() {
		setHardness(6.0F);
		setStepSound(soundTypeMetal);
		setBlockName("energyTurret");
		setBlockTextureName("opensecurity:machine_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float clickX, float clickY, float clickZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		player.openGui(OpenSecurity.instance, 2, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)  {
		return new TileEntityEnergyTurret();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.iron_block.getIcon(side, meta);
	}

	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPramater) {
		OpenSecurity.logger.info("BLOCK EVENT");
		return true;
	}
}