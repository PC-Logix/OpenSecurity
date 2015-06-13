/**
 * 
 */
package pcl.opensecurity.blocks;

import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.tileentity.TileEntityAlarm;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockMagReader extends BlockContainer {

	public BlockMagReader() {
		super(Material.iron);
		setBlockName("magreader");
		setBlockTextureName("opensecurity:magreader");
	}
	
	private TileEntityMagReader tile;
	
	@Override
	public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		tile = (TileEntityMagReader) world.getTileEntity(xCoord, yCoord, zCoord);
		if (!world.isRemote) {
			if (equipped instanceof ItemMagCard){
				tile.doRead(entityplayer.getCurrentEquippedItem());
			}
		}
		return true;
	}
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityMagReader();
	}
}