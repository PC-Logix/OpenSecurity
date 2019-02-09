package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.common.tileentity.TileEntityRolldoor;

import javax.annotation.Nonnull;
import java.util.List;

public class BlockRolldoor extends BlockOSBase {
    public final static String NAME = "rolldoor";

    public BlockRolldoor() {
        super(NAME, Material.IRON, 0.5f);
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBoundingBox, @Nonnull List<AxisAlignedBB> stacks, Entity entity, boolean isActualState) {

        TileEntityRolldoor rolldoor = getTileEntity(world, pos);

        if(rolldoor != null)
            addCollisionBoxToList(pos, entityBoundingBox, stacks, rolldoor.getBoundingBox());

        addCollisionBoxToList(pos, entityBoundingBox, stacks, FULL_BLOCK_AABB);
    }

    @Override
    @Deprecated
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    TileEntityRolldoor getTileEntity(IBlockAccess world, BlockPos pos){
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

        TileEntityRolldoor tile = getTileEntity(world, pos);

        if(tile != null)
            tile.initialize();
    }

}
