package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityDataBlock;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;

public class BlockDoorController extends BlockOSBase {

	public BlockDoorController(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("door_controller");
		setRegistryName("door_controller");
		setHardness(.5f);
		random = new Random();
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		TileEntity te = world.getTileEntity(pos);
		((TileEntityDoorController) te).setOwner(placer.getUniqueID().toString());
		//((TileEntityDoorController) te).overrideTexture(ContentRegistry.doorController, new ItemStack(Item.getItemFromBlock(ContentRegistry.doorController)), ForgeDirection.getOrientation(1));
		EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.fromAngle(placer.rotationYaw);
		return this.getDefaultState().withProperty(PROPERTYFACING, enumfacing);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityDoorController();
	}

}
