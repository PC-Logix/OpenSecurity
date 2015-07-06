package pcl.opensecurity.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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
	
	@SideOnly(Side.CLIENT)
	public static IIcon texture;
	
	@SideOnly(Side.CLIENT)
	public static IIcon texture_active;
	
	@SideOnly(Side.CLIENT)
	public static IIcon texture_found;
	
	@SideOnly(Side.CLIENT)
	public static IIcon texture_nofound;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		texture = icon.registerIcon("opensecurity:rfidreader");
		texture_active = icon.registerIcon("opensecurity:rfidreader_active");
		texture_found = icon.registerIcon("opensecurity:rfidreader_found");
		texture_nofound = icon.registerIcon("opensecurity:rfidreader_nofound");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
			switch(metadata) {
			case 1:
				return texture_active;
			case 2:
				return texture_found;
			case 3:
				return texture_nofound;
			default:
				return texture;
			}
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityRFIDReader();
	}
	
	@Override
	public void updateTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
		world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
	}
}
