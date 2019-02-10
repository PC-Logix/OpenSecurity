package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorElement;

public class BlockRolldoorElement extends Block implements ITileEntityProvider {
    public final static String NAME = "rolldoor_element";

    public BlockRolldoorElement(){
        super(Material.IRON);
        setUnlocalizedName(NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setHardness(31337f);
        setBlockUnbreakable();
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityRolldoorElement();
    }

    public TileEntityRolldoorElement getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityRolldoorElement ? (TileEntityRolldoorElement) tile : null;
    }

    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        TileEntityRolldoorElement tile = getTileEntity(source, pos);
        return tile != null ? tile.getBoundingBox() : FULL_BLOCK_AABB;
    }

    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
}
