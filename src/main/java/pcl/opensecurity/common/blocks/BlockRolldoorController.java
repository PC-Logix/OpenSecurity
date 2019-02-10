package pcl.opensecurity.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class BlockRolldoorController extends BlockDoorController {
    public static final String NAME = "rolldoor_controller";

    public BlockRolldoorController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityRolldoorController();
    }

    TileEntityRolldoorController getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityRolldoorController ? (TileEntityRolldoorController) tile : null;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(!world.isRemote) {
            TileEntityRolldoorController tile = getTileEntity(world, pos);

            if (tile != null)
                tile.initialize();
        }
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if(!world.isRemote) {
            TileEntityRolldoorController tile = getTileEntity(world, pos);
            if (tile != null)
                tile.remove();
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return FULL_BLOCK_AABB;
    }
}
