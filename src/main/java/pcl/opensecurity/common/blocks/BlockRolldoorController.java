package pcl.opensecurity.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class BlockRolldoorController extends BlockCamouflage implements ITileEntityProvider {
    public static final String NAME = "rolldoor_controller";
    public static BlockRolldoorController DEFAULTITEM;

    public BlockRolldoorController() {
        super(Material.IRON, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
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

    @Override
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

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {

        TileEntityRolldoorController tile = getTileEntity(world, pos);
        if(tile != null && tile.setColor(player.getHeldItemMainhand())) {
            return true;
        }

        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState iBlockState) {
        return true;
    }

}
