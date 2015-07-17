package pcl.opensecurity.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCardWriter extends BlockOSBase {

	public BlockCardWriter() {
		setBlockName("cardwriter");
	}

	@SideOnly(Side.CLIENT)
	public IIcon faceIcon;
	@SideOnly(Side.CLIENT)
	public IIcon sideIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		faceIcon = icon.registerIcon("opensecurity:cardwriter_top");
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

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCardWriter();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float clickX, float clickY, float clickZ) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
		player.openGui(OpenSecurity.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityCardWriter tileEntity = (TileEntityCardWriter) world.getTileEntity(x, y, z);
		dropContent(tileEntity, world, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
		super.breakBlock(world, x, y, z, block, meta);
	}

	public void dropContent(IInventory chest, World world, int xCoord, int yCoord, int zCoord) {
		if (chest == null)
			return;
		Random random = new Random();
		for (int i1 = 0; i1 < chest.getSizeInventory(); ++i1) {
			ItemStack itemstack = chest.getStackInSlot(i1);

			if (itemstack != null) {
				float offsetX = random.nextFloat() * 0.8F + 0.1F;
				float offsetY = random.nextFloat() * 0.8F + 0.1F;
				float offsetZ = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;

				for (; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
					int stackSize = random.nextInt(21) + 10;
					if (stackSize > itemstack.stackSize)
						stackSize = itemstack.stackSize;

					itemstack.stackSize -= stackSize;
					entityitem = new EntityItem(world, xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, new ItemStack(itemstack.getItem(), stackSize, itemstack.getItemDamage()));

					float velocity = 0.05F;
					entityitem.motionX = (float) random.nextGaussian() * velocity;
					entityitem.motionY = (float) random.nextGaussian() * velocity + 0.2F;
					entityitem.motionZ = (float) random.nextGaussian() * velocity;

					if (itemstack.hasTagCompound())
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
				}
			}
		}
	}
}