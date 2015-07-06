/**
 * 
 */
package pcl.opensecurity.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockMagReader extends BlockContainer {

	public BlockMagReader() {
		super(Material.iron);
		setBlockName("magreader");
		//setBlockTextureName("opensecurity:magreader");
	}
	
	
	@SideOnly(Side.CLIENT)
	public static IIcon topIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon bottomIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon_idle;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon_activated;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon_error;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon_success;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		topIcon = icon.registerIcon("opensecurity:machine_bottom");
		bottomIcon = icon.registerIcon("opensecurity:machine_bottom");
		sideIcon_idle = icon.registerIcon("opensecurity:magreader");
		sideIcon_activated = icon.registerIcon("opensecurity:magreader_active");
		sideIcon_error = icon.registerIcon("opensecurity:magreader_error");
		sideIcon_success = icon.registerIcon("opensecurity:magreader_success");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if(side == 0) {
			return bottomIcon;
		} else if(side == 1) {
			return topIcon;
		} else {
			switch(metadata) {
			case 1:
				return sideIcon_activated;
			case 2:
				return sideIcon_error;
			case 3:
				return sideIcon_success;
			default:
				return sideIcon_idle;
			}
		}
	}
	
	@Override
	public void updateTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
		world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
	}
	
	@Override
	public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem() : null;
		TileEntityMagReader tile = (TileEntityMagReader) world.getTileEntity(xCoord, yCoord, zCoord);
		if (!world.isRemote) {
			if (equipped instanceof ItemMagCard){
				if(tile.doRead(entityplayer.getCurrentEquippedItem(), entityplayer)) {
					world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3, 1);
				} else {
					world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2, 1);
				}
				world.scheduleBlockUpdate(xCoord, yCoord, zCoord, this, 30);
			}
		}
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityMagReader();
	}
}