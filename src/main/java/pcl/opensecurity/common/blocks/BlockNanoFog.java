package pcl.opensecurity.common.blocks;

/* based on McJty's RFTools Shield */

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.nanofog.*;
import pcl.opensecurity.common.tileentity.TileEntityNanoFog;
import pcl.opensecurity.util.BlockUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;


public class BlockNanoFog extends Block implements ITileEntityProvider {
    public static final String NAME = "nanofog";
    public static final String CAMO = "camo";
    public static final CamoProperty CAMOID = new CamoProperty("camoid");

    // shrinked cubic bounding box so that entities can collide
    static final AxisAlignedBB boundingBox = new AxisAlignedBB(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public BlockNanoFog(){
        super(Material.GLASS);
        setLightOpacity(255);
        setRegistryName(NAME);
        setUnlocalizedName(NAME);
        setBlockUnbreakable();
        setResistance(31337.0F);
    }

    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
        return boundingBox;
    }

    @Override
    @Deprecated
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBoundingBox, @Nonnull List<AxisAlignedBB> stacks, Entity entity, boolean isActualState) {
        if(!canEntityPass(world, pos, entity))
            super.addCollisionBoxToList(state, world, pos, entityBoundingBox, stacks, entity, isActualState);
    }

    private boolean canEntityPass(World world, BlockPos pos, Entity entity){
        if(entity == null)
            return false;

        TileEntityNanoFog te = getTE(world, pos);
        return te != null && (!te.isSolid() || te.filterPass.contains(entity.getClass(), entity.getName()));
    }

    @Override
    @Deprecated
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState block = world.getBlockState(pos.offset(side));
        TileEntityNanoFog te = getTE(world, pos);

        if(!te.isBuild())
            return false;

        if(block.getBlock() instanceof BlockNanoFog){
            TileEntityNanoFog teOther = getTE(world, pos.offset(side));
            return te.camoId != teOther.camoId || te.camoMeta != teOther.camoMeta;
        }

        return true;
    }

    public static TileEntityNanoFog getTE(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityNanoFog ? (TileEntityNanoFog) te : null;
    }



    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityNanoFog();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        // To make sure that our ISBM model is chosen for all states we use this custom state mapper:
        StateMapperBase ignoreState = new StateMapperBase() {
            @Override
            @Nonnull
            protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState iBlockState) {
                return NanoFogBakedModel.modelFacade;
            }
        };
        ModelLoader.setCustomStateMapper(this, ignoreState);
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

    @Override
    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        return false;
    }

    @Override
    public void onBlockExploded(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    @Nonnull
    @Deprecated
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.BLOCK;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true; // delegated to CamoBakedModel#getQuads
    }

    @Nullable
    protected IBlockState getMimicBlock(IBlockAccess blockAccess, BlockPos pos) {
        TileEntity te = blockAccess.getTileEntity(pos);
        return te instanceof TileEntityNanoFog ? ((TileEntityNanoFog) te).getMimicBlock() : null;
    }

    @SideOnly(Side.CLIENT)
    public void initColorHandler(BlockColors blockColors) {
        blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
            IBlockState mimicBlock = getMimicBlock(world, pos);
            return mimicBlock != null ? blockColors.colorMultiplier(mimicBlock, world, pos, tintIndex) : -1;
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
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if(entity == null)
            return;

        if(entity.world.isRemote)
            return;

        TileEntityNanoFog te = getTE(world, pos);
        if(te == null)
            return;

        if(te.getDamage() > 0 && te.filterDamage.contains(entity.getClass(), entity.getName())) {
            if(te.getTerminal().consumeDamageEnergy(te.getDamage()))
                entity.attackEntityFrom(new DamageSource("NanoFog"), te.getDamage());
        }

        if(te.getKnockback() > 0 && !canEntityPass(world, pos, entity)) {
            if (te.getTerminal().consumeKnockbackEnergy(te.getKnockback())) {
                Vec3i kbVec = BlockUtils.getFacingFromEntity(pos, entity);
                entity.addVelocity(kbVec.getX() * te.getKnockback(), kbVec.getY() * te.getKnockback(), kbVec.getZ() * te.getKnockback());
            }
        }
    }

}
