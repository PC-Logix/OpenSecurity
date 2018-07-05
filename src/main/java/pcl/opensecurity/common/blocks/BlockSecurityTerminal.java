package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.Reference;
import pcl.opensecurity.common.tileentity.TileEntitySecurityTerminal;

public class BlockSecurityTerminal extends BlockOSBase {

    public BlockSecurityTerminal() {
        super(Reference.Names.BLOCK_SECURITY_TERMINAL, Material.IRON, 1f);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        ((TileEntitySecurityTerminal) te).setOwner(placer.getUniqueID().toString());

    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySecurityTerminal();
    }
}
