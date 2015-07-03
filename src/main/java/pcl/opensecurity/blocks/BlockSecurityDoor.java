package pcl.opensecurity.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSecurityDoor extends BlockDoor
{
	public Item placerItem;
	public IIcon topDoorIcon;
	public IIcon[] flippedIcons = new IIcon[2];

	public BlockSecurityDoor()
	{
		super(Material.iron);
		float f = 0.5F;
		float f1 = 1.0F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		this.setHardness(50F);
		this.setResistance(6000F);
		this.setBlockName("securityDoor");
	}

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		if (par5 == 1 || par5 == 0)
		{
			return this.blockIcon;
		}
		int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		boolean flag = (meta & 4) != 0;
		int halfMeta = meta & 3;
		boolean flipped = false;

		if (flag)
		{
			if (halfMeta == 0 && par5 == 2) flipped = !flipped;
			else if (halfMeta == 1 && par5 == 5) flipped = !flipped;
			else if (halfMeta == 2 && par5 == 3) flipped = !flipped;
			else if (halfMeta == 3 && par5 == 4) flipped = !flipped;
		}
		else
		{
			if (halfMeta == 0 && par5 == 5) flipped = !flipped;
			else if (halfMeta == 1 && par5 == 3) flipped = !flipped;
			else if (halfMeta == 2 && par5 == 4) flipped = !flipped;
			else if (halfMeta == 3 && par5 == 2)flipped = !flipped;
			if ((meta & 16) != 0)flipped = !flipped;
		}

		if (flipped) return flippedIcons[(meta & 8) != 0 ? 1 : 0];
		else return (meta & 8) != 0 ? this.topDoorIcon : this.blockIcon;
	}
    
    @SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2)
	{
		return this.blockIcon;
	}

    @SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.blockIcon = iconRegister.registerIcon(OpenSecurity.MODID + ":door_secure_lower");
		this.topDoorIcon = iconRegister.registerIcon(OpenSecurity.MODID + ":door_secure_upper");
		this.flippedIcons[0] = new IconFlipped(blockIcon, true, false);
		this.flippedIcons[1] = new IconFlipped(topDoorIcon, true, false);
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
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
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);

        if ((l & 8) == 0)
        {
            boolean flag = false;

            if (p_149695_1_.getBlock(p_149695_2_, p_149695_3_ + 1, p_149695_4_) != this)
            {
                p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
                flag = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_))
            {
                p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
                flag = true;

                if (p_149695_1_.getBlock(p_149695_2_, p_149695_3_ + 1, p_149695_4_) == this)
                {
                    p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_ + 1, p_149695_4_);
                }
            }

            if (flag)
            {
                if (!p_149695_1_.isRemote)
                {
                    this.dropBlockAsItem(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, l, 0);
                }
            }
        }
        else
        {
            if (p_149695_1_.getBlock(p_149695_2_, p_149695_3_ - 1, p_149695_4_) != this)
            {
                p_149695_1_.setBlockToAir(p_149695_2_, p_149695_3_, p_149695_4_);
            }

            if (p_149695_5_ != this)
            {
                this.onNeighborBlockChange(p_149695_1_, p_149695_2_, p_149695_3_ - 1, p_149695_4_, p_149695_5_);
            }
        }
    }
}