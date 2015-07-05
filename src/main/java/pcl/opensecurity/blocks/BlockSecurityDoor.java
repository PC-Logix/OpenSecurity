package pcl.opensecurity.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSecurityDoor extends BlockDoor implements ITileEntityProvider
{
	public Item placerItem;

	@SideOnly(Side.CLIENT)
	private IIcon[] iconsUpper;
	@SideOnly(Side.CLIENT)
	private IIcon[] iconsLower;

	public BlockSecurityDoor()
	{
		super(Material.iron);
		float f = 0.5F;
		float f1 = 1.0F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		this.setHardness(5F);
		this.setResistance(6000F);
		this.setBlockName("securityDoor");
		this.setBlockTextureName(OpenSecurity.MODID + ":door_secure_upper");
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int par5)
	{
		if (par5 != 1 && par5 != 0)
		{
			int blockMeta = this.func_150012_g(blockAccess, x, y, z);
			int j1 = blockMeta & 3;
			boolean isLowerPanel = (blockMeta & 4) != 0;
			boolean flipped = false;
			boolean isUpperPanel = (blockMeta & 8) != 0;

			if (isLowerPanel)
			{
				if (j1 == 0 && par5 == 2) flipped = !flipped;
				else if (j1 == 1 && par5 == 5) flipped = !flipped;
				else if (j1 == 2 && par5 == 3) flipped = !flipped;
				else if (j1 == 3 && par5 == 4) flipped = !flipped;
			}
			else
			{
				if (j1 == 0 && par5 == 5) flipped = !flipped;
				else if (j1 == 1 && par5 == 3) flipped = !flipped;
				else if (j1 == 2 && par5 == 4) flipped = !flipped;
				else if (j1 == 3 && par5 == 2) flipped = !flipped;

				if ((blockMeta & 16) != 0)
				{
					flipped = !flipped;
				}
			}

			return isUpperPanel ? this.iconsUpper[flipped?1:0] : this.iconsLower[flipped?1:0];
		}
		else
		{
			return this.iconsLower[0];
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconsUpper = new IIcon[2];
		this.iconsLower = new IIcon[2];
		this.iconsUpper[0] = p_149651_1_.registerIcon(OpenSecurity.MODID + ":door_secure_upper");
		this.iconsLower[0] = p_149651_1_.registerIcon(OpenSecurity.MODID + ":door_secure_lower");
		this.iconsUpper[1] = new IconFlipped(this.iconsUpper[0], true, false);
		this.iconsLower[1] = new IconFlipped(this.iconsLower[0], true, false);
	}

	@Override
	public boolean onBlockActivated(World world, int par2, int par3, int par4, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	{
		return OpenSecurity.securityDoor;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(OpenSecurity.securityDoor);

	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) == 0) //Top door block
		{
			boolean breakBlock = false;

			if (world.getBlock(x, y + 1, z) != this)
			{
				world.setBlockToAir(x, y, z);
				breakBlock = true;
			}

			if (!World.doesBlockHaveSolidTopSurface(world, x, y - 1, z))
			{
				world.setBlockToAir(x, y, z);
				breakBlock = true;

				if (world.getBlock(x, y + 1, z) == this)
				{
					world.setBlockToAir(x, y + 1, z);
				}
			}

			if (breakBlock)
			{
				if (!world.isRemote)
				{
					this.dropBlockAsItem(world, x, y, z, meta, 0);
				}
			}
		}
		else //Bottom door block
		{
			if (world.getBlock(x, y - 1, z) != this)
			{
				world.setBlockToAir(x, y, z);
			}

			if (block != this)
			{
				this.onNeighborBlockChange(world, x, y - 1, z, block);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntitySecureDoor();
	}
	
	
}