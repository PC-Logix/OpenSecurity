package pcl.opensecurity.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.tileentity.TileEntitySwitchableHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwitchableHub extends BlockOSBase {

	@SideOnly(Side.CLIENT)
	private IIcon inputNoCon;
	@SideOnly(Side.CLIENT)
	private IIcon inputCon;
	@SideOnly(Side.CLIENT)
	private IIcon outputNoCon;
	@SideOnly(Side.CLIENT)
	private IIcon outputDis;
	@SideOnly(Side.CLIENT)
	private IIcon outputCon;
	
	public BlockSwitchableHub() {
		setBlockName("switchablehub");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntitySwitchableHub();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		inputNoCon = icon.registerIcon("opensecurity:hub_input_noconnected");
		inputCon = icon.registerIcon("opensecurity:hub_input_connected");
		outputNoCon = icon.registerIcon("opensecurity:hub_output_noconnected");
		outputDis = icon.registerIcon("opensecurity:hub_output_disabled");
		outputCon = icon.registerIcon("opensecurity:hub_output_connected");
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int metadata, int side) {
		if (side == 4 && metadata == 0) {
			return this.inputCon;
		}
		return this.outputNoCon;
	}
	
	
	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess block, int x, int y, int z, int side) {
		int metadata = block.getBlockMetadata(x, y, z);
		TileEntitySwitchableHub switchTE = (TileEntitySwitchableHub) block.getTileEntity(x, y, z);
	
		if (metadata == 0 && side == 0)
			return this.inputCon;
		else if (metadata == 1 && side == 1)
			return this.inputCon;
		else if (metadata == 2 && side == 2)
			return this.inputCon;
		else if (metadata == 3 && side == 3)
			return this.inputCon;
		else if (metadata == 4 && side == 4)
			return this.inputCon;
		else if (metadata == 5 && side == 5)
			return this.inputCon;
		
		
		
		if (switchTE.down && side == 0)
			return this.outputCon;
		else if (switchTE.up && side == 1)
			return this.outputCon;
		else if (switchTE.north && side == 2)
			return this.outputCon;
		else if (switchTE.south && side == 3)
			return this.outputCon;
		else if (switchTE.west && side == 4)
			return this.outputCon;
		else if (switchTE.east && side == 5)
			return this.outputCon;
		
		
			return this.outputNoCon;
	}

}
