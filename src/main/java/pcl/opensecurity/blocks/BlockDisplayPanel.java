/**
 * 
 */
package pcl.opensecurity.blocks;

import pcl.opensecurity.tileentity.TileEntityDisplayPanel;
import pcl.opensecurity.tileentity.TileEntityKVM;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockDisplayPanel extends BlockOSBase {

	public BlockDisplayPanel() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityDisplayPanel();
	}
}
