package pcl.opensecurity.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorController;

public class BlockRolldoor extends BlockCamouflage implements ITileEntityProvider {
    public final static String NAME = "rolldoor";
    public final static AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    public BlockRolldoor() {
        super(Material.IRON, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return emptyBB;
    }

    TileEntityRolldoor getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityRolldoor ? (TileEntityRolldoor) tile : null;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRolldoor();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(!world.isRemote) {
            TileEntityRolldoor tile = getTileEntity(world, pos);

            if (tile != null) {
                tile.initialize();
            }
        }
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        TileEntityRolldoorController controller = null;
        if(!world.isRemote) {
            TileEntityRolldoor tile = getTileEntity(world, pos);
            if (tile != null) {
                tile.remove();
                controller = tile.getController();
            }
        }

        boolean wasRemoved = super.removedByPlayer(state, world, pos, player, willHarvest);

        if(wasRemoved && controller != null)
            controller.initialize();

        return wasRemoved;
    }





}