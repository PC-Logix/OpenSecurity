package pcl.opensecurity.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityBiometricReader;
import pcl.opensecurity.common.tileentity.TileEntityCardWriter;
import pcl.opensecurity.common.tileentity.TileEntityDataBlock;

public class BlockCardWriter extends BlockOSBase {

	public static final int GUI_ID = 1;
	
	public BlockCardWriter(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("card_writer");
		setRegistryName("card_writer");
		setHardness(.5f);
		random = new Random();
	}
		
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCardWriter();
	}
	
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityCardWriter)) {
            return false;
        }
        player.openGui(OpenSecurity.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }
}
