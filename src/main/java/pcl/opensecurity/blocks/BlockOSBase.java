package pcl.opensecurity.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockOSBase extends BlockContainer {

	protected BlockOSBase() {
		super(Material.iron);
		this.setHardness(5F);
		this.setResistance(30F);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		super.onBlockPlacedBy(par1World, x, y, z, par5EntityLivingBase, par6ItemStack);
		int whichDirectionFacing = 0;
		if (par5EntityLivingBase.rotationPitch >= 70) {
			whichDirectionFacing = 0;// down
		} else if (par5EntityLivingBase.rotationPitch <= -70) {
			whichDirectionFacing = 1;// up
		} else {
			whichDirectionFacing = (MathHelper.floor_double(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3);
			switch (whichDirectionFacing) {
			case 0:
				whichDirectionFacing = ForgeDirection.SOUTH.ordinal();
				break;
			case 1:
				whichDirectionFacing = ForgeDirection.WEST.ordinal();
				break;
			case 2:
				whichDirectionFacing = ForgeDirection.NORTH.ordinal();
				break;
			case 3:
				whichDirectionFacing = ForgeDirection.EAST.ordinal();
				break;
			}
		}
		par1World.setBlockMetadataWithNotify(x, y, z, par5EntityLivingBase.isSneaking() ? whichDirectionFacing : ForgeDirection.OPPOSITES[whichDirectionFacing], 2);
	}

}
