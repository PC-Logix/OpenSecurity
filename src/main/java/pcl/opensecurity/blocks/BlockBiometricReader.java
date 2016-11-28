package pcl.opensecurity.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.items.ItemMagCard;
import pcl.opensecurity.tileentity.TileEntityBiometricReader;
import pcl.opensecurity.tileentity.TileEntityMagReader;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class BlockBiometricReader extends BlockOSBase {

	public BlockBiometricReader() {
		setBlockName("biometricreader");
		setBlockTextureName("opensecurity:biometricreader");
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon faceIcon;
	@SideOnly(Side.CLIENT)
	public IIcon faceIcon_active;
	@SideOnly(Side.CLIENT)
	public IIcon sideIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		faceIcon = icon.registerIcon("opensecurity:biometricreader_top");
		faceIcon_active = icon.registerIcon("opensecurity:biometricreader_top_active");
		sideIcon = icon.registerIcon("opensecurity:machine_side");
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
		if (metadata == side) {
			return this.faceIcon;
		} else
			return this.sideIcon;
	}
	
	@Override
	public boolean onBlockActivated(World world, int xCoord, int yCoord, int zCoord, EntityPlayer entityplayer, int side, float clickedX, float clickedY, float clickedZ) {
		TileEntityBiometricReader tile = (TileEntityBiometricReader) world.getTileEntity(xCoord, yCoord, zCoord);
		if (!world.isRemote && side == world.getBlockMetadata(xCoord, yCoord, zCoord)) {
			tile.doRead(entityplayer, side);
			return true;
		}
		if (side == world.getBlockMetadata(xCoord, yCoord, zCoord)) {
			return true;
		}
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityBiometricReader();
	}
}
