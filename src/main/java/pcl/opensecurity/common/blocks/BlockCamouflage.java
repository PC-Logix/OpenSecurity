package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.camouflage.CamoBlockId;
import pcl.opensecurity.common.camouflage.CamoProperty;
import pcl.opensecurity.util.ICamo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockCamouflage extends Block {
    public static final String CAMO = "camo";
    public static final CamoProperty CAMOID = new CamoProperty("camoid");

    public BlockCamouflage(Material materialIn, String name){
        super(materialIn);
        setRegistryName(name);
        setUnlocalizedName(name);
    }

    public static ICamo getTE(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof ICamo ? (ICamo) te : null;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        ICamo te = getTE(worldIn, pos);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(ModelResourceLocation model) {
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            @Nonnull
            protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState iBlockState) {
                return model;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true; // delegated to CamoBakedModel#getQuads
    }

    @Nullable
    protected static IBlockState getMimicBlock(IBlockAccess blockAccess, BlockPos pos) {
        TileEntity te = blockAccess.getTileEntity(pos);
        return te instanceof ICamo ? ((ICamo) te).getCamoBlock() : null;
    }

    @SideOnly(Side.CLIENT)
    public void initColorHandler(BlockColors blockColors) {
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            IBlockState mimicBlock = getMimicBlock(world, pos);
            if(mimicBlock == null)
                return -1;

            if(mimicBlock.getBlock().equals(this))
                return 0;

            return blockColors.colorMultiplier(mimicBlock, world, pos, tintIndex);
        }, this);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] { }, new IUnlistedProperty[] { CAMOID });
    }

    @Override
    @Nonnull
    public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        return getExtendedStateMimic((IExtendedBlockState) state, world, pos);
    }

    private IExtendedBlockState getExtendedStateMimic(IExtendedBlockState state, IBlockAccess world, BlockPos pos){
        IBlockState mimicBlock = getMimicBlock(world, pos);
        return mimicBlock != null ? state.withProperty(CAMOID, new CamoBlockId(mimicBlock)) : state;
    }

    @Override
    @Deprecated
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState block = world.getBlockState(pos.offset(side));

        if(block.getBlock() instanceof BlockCamouflage){
            ICamo te = getTE(world, pos);
            ICamo teOther = getTE(world, pos.offset(side));
            return !te.getCamoBlock().getBlock().equals(teOther.getCamoBlock().getBlock());
        }

        return true;
    }


}
