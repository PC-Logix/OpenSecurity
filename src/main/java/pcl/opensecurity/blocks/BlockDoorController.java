package pcl.opensecurity.blocks;

import li.cil.oc.common.item.Wrench;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityDoorController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDoorController extends BlockOSBase {

	public BlockDoorController() {
		super();
		setBlockName("doorController");
		setBlockTextureName("opensecurity:door_controller");
		this.setHardness(5F);
		this.setResistance(6000F);
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		TileEntity te = par1World.getTileEntity(x, y, z);
		((TileEntityDoorController) te).setOwner(par5EntityLivingBase.getUniqueID().toString());
		((TileEntityDoorController) te).overrideTexture(OpenSecurity.DoorController, new ItemStack(Item.getItemFromBlock(OpenSecurity.DoorController)), ForgeDirection.getOrientation(1));
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float clickX, float clickY, float clickZ) {
		TileEntityDoorController tileEntity = (TileEntityDoorController) world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}

		//If the user is not the owner, or the user is not in creative drop out.
		if(tileEntity.getOwner()!=null){
			if(!tileEntity.getOwner().equals(player.getUniqueID().toString()) && !player.capabilities.isCreativeMode) {
				if(!tileEntity.getOwner().isEmpty()) {
					return false;
				}
			}
		}
		//Change the block texture
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBlock) {
			Block block = Block.getBlockFromItem(player.getCurrentEquippedItem().getItem());
			Item oldBlock = tileEntity.DoorControllerCamo[0].getItem();
			if (block.isOpaqueCube() || block.equals(Blocks.glass)) {
				if (!tileEntity.DoorControllerCamo[0].getItem().equals(player.getCurrentEquippedItem().getItem())) {
					tileEntity.overrideTexture(block, player.getCurrentEquippedItem().splitStack(1), ForgeDirection.getOrientation(side));
					world.scheduleBlockUpdate(x, y, z, tileEntity.block, 5);
					if (!world.isRemote) {
						ItemStack testAgainst = new ItemStack(oldBlock);
						if (!testAgainst.getItem().equals(Item.getItemFromBlock(OpenSecurity.DoorController))) {
							EntityItem myItemEntity = new EntityItem(world, x, y, z, testAgainst);
							world.spawnEntityInWorld(myItemEntity);
						}
					}
				}
			}
			//Remove the block texture with the scrench
		} else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof Wrench) {
			if (!world.isRemote) {
				if (!tileEntity.DoorControllerCamo[0].getItem().equals(Item.getItemFromBlock(OpenSecurity.DoorController))) {
					EntityItem myItemEntity = new EntityItem(world, x, y, z, tileEntity.DoorControllerCamo[0]);
					world.spawnEntityInWorld(myItemEntity);
				}
			}
			tileEntity.overrideTexture(OpenSecurity.DoorController, new ItemStack(Item.getItemFromBlock(OpenSecurity.DoorController)), ForgeDirection.getOrientation(side));
			world.scheduleBlockUpdate(x, y, z, tileEntity.block, 5);
		}

		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityDoorController();
	}

	@SideOnly(Side.CLIENT)
	public static IIcon topIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon bottomIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon leftIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon rightIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon frontIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon backIcon;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		topIcon = icon.registerIcon("opensecurity:machine_side");
		bottomIcon = icon.registerIcon("opensecurity:machine_side");
		leftIcon = rightIcon = frontIcon = backIcon = icon.registerIcon("opensecurity:door_controller");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		if (side == 0) {
			return bottomIcon;
		} else if (side == 1) {
			return topIcon;
		} else if (side == 2) {
			return backIcon;
		} else if (side == 3) {
			return frontIcon;
		} else if (side == 4) {
			return rightIcon;
		} else if (side == 5) {
			return leftIcon;
		}
		return leftIcon;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess block, int x, int y, int z, int side) {
		int metadata = block.getBlockMetadata(x, y, z);
		TileEntityDoorController te = (TileEntityDoorController) block.getTileEntity(x, y, z);
		IIcon thisBlockTextures[] = new IIcon[6];

		if (te.DoorControllerCamo[0] != null) {
			te.overrideTexture(te.DoorControllerCamo[0]);
		}
		for (int getSide = 0; getSide < thisBlockTextures.length; getSide++) {
			if(te.blockTextures[getSide] != null) {
				if(side == getSide) {
					return te.blockTextures[getSide];
				}
			}
		}			


		return topIcon;
	}

}
