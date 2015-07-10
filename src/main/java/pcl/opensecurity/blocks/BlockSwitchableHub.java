package pcl.opensecurity.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pcl.opensecurity.tileentity.TileEntitySwitchableHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwitchableHub extends BlockOSBase {

	@SideOnly(Side.CLIENT)
	private IIcon topAndBottomIcon;
	@SideOnly(Side.CLIENT)
	private IIcon faceIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;

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
		faceIcon = icon.registerIcon("opensecurity:hub_input_noconnected");
		sideIcon = icon.registerIcon("opensecurity:hub_output_noconnected");
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (side == 4 && metadata == 0) {
			return this.faceIcon;
		}
		if (metadata == 1 && side == 1)
			return this.faceIcon;
		else if (metadata == 0 && side == 0)
			return this.faceIcon;
		else if (metadata == 2 && side == 2)
			return this.faceIcon;
		else if (metadata == 3 && side == 3)
			return this.faceIcon;
		else if (metadata == 4 && side == 4)
			return this.faceIcon;
		else if (metadata == 5 && side == 5)
			return this.faceIcon;
		else
			return this.sideIcon;
	}

}
