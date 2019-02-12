package pcl.opensecurity.common.blocks;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.tileentity.TileEntityRolldoorElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class BlockRolldoorElement extends BlockOSBase {
    public static final String NAME = "rolldoor_element";
    public static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    public static final PropertyInteger PROPERTYOFFSET = PropertyInteger.create("offset", 0, 15);

    public BlockRolldoorElement(){
        super(NAME, Material.IRON, 0.5f);
        setBlockUnbreakable();
        setResistance(31337f);
        setCreativeTab(null); //dont assign any creative tab so the element wouldnt show in creative/JEI
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items){
        //dont assign any sub blocks so the element wouldnt show in creative/JEI
    }

    // avoid to connect to fences/glass panes
    @Override
    @Deprecated
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.CENTER;
    }

    // ignore piston movement
    @Override
    @Deprecated
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityRolldoorElement();
    }

    public TileEntityRolldoorElement getTileEntity(IBlockAccess world, BlockPos pos){
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityRolldoorElement ? (TileEntityRolldoorElement) tile : null;
    }

    @Override
    @Deprecated
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        TileEntityRolldoorElement element = getTileEntity(source, pos);
        return element != null ? element.getBoundingBox() : FULL_BLOCK_AABB;
    }

    @Override
    @Deprecated
    @Nonnull
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return emptyBB;
    }

    @Nullable
    @Override
    @Deprecated
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos){
        AxisAlignedBB bb = getBoundingBox(blockState, worldIn, pos);
        return !bb.equals(emptyBB) ? bb : NULL_AABB;
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

    @Override
    @Deprecated
    // this is used to determine if a player should take damage from standing inside a wall
    public boolean causesSuffocation(IBlockState state){ return false; }


    /* offset blockstate */
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        int offset = Math.min(Math.max(0, meta), 15);
        return getDefaultState().withProperty(PROPERTYOFFSET, offset);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getOffset(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTYOFFSET);
    }

    @Override
    @Deprecated
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(PROPERTYOFFSET, 0);
    }

    public static int getOffset(IBlockState state){
        return state.getValue(PROPERTYOFFSET);
    }


}
