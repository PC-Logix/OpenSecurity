package pcl.opensecurity.blocks;

import java.util.Random;

import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSecurityDoorPrivate extends BlockSecurityDoor {
	
	@SideOnly(Side.CLIENT)
	private IIcon[] iconsUpper;
	@SideOnly(Side.CLIENT)
	private IIcon[] iconsLower;
	
	public BlockSecurityDoorPrivate()
	{
		super();
		float f = 0.5F;
		float f1 = 1.0F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
		this.setHardness(400F);
		this.setResistance(6000F);
		this.setBlockName("securityDoor");
		this.setBlockTextureName(OpenSecurity.MODID + ":door_secure_upper_nowindow");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.iconsUpper = new IIcon[2];
		this.iconsLower = new IIcon[2];
		this.iconsUpper[0] = p_149651_1_.registerIcon(OpenSecurity.MODID + ":door_secure_upper_nowindow");
		this.iconsLower[0] = p_149651_1_.registerIcon(OpenSecurity.MODID + ":door_secure_lower");
		this.iconsUpper[1] = new IconFlipped(this.iconsUpper[0], true, false);
		this.iconsLower[1] = new IconFlipped(this.iconsLower[0], true, false);
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		TileEntitySecureDoor tileEntity = (TileEntitySecureDoor) world.getTileEntity(x, y, z);
		//If the user is not the owner, or the user is not in creative drop out.
		if((tileEntity.getOwner()!=null && tileEntity.getOwner().equals(player.getUniqueID().toString())) || player.capabilities.isCreativeMode){
			this.setResistance(4F);
			this.setHardness(6F);
		} else {
			this.setResistance(400F);
			this.setHardness(6000F);
		}
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
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ContentRegistry.securityDoorPrivateItem;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(ContentRegistry.securityDoorPrivateItem);

	}
}
