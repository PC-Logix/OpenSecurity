package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;

public class BlockDoorController extends BlockOSBase {

	public BlockDoorController(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("door_controller");
		setRegistryName("door_controller");
		setHardness(.5f);
		random = new Random();
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		TileEntityDoorController te = (TileEntityDoorController) worldIn.getTileEntity(pos);
		te.rescan(pos);
	}
	
    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
		TileEntity te = worldIn.getTileEntity(pos);
		((TileEntityDoorController) te).setOwner(placer.getUniqueID().toString());
		((TileEntityDoorController) te).rescan(pos);
		//((TileEntityDoorController) te).overrideTexture(ContentRegistry.doorController, new ItemStack(Item.getItemFromBlock(ContentRegistry.doorController)), ForgeDirection.getOrientation(1));
    }

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityDoorController();
	}

}
