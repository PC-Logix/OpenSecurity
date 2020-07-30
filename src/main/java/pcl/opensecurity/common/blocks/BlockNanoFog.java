package pcl.opensecurity.common.blocks;

/* based on McJty's RFTools Shield */

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import pcl.opensecurity.common.tileentity.TileEntityNanoFog;
import pcl.opensecurity.util.BlockUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

@Optional.InterfaceList({
    @Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock", modid = "galacticraftcore")
})
public class BlockNanoFog extends BlockCamouflage implements ITileEntityProvider, IPartialSealableBlock {
    public static final String NAME = "nanofog";
    public static BlockNanoFog DEFAULTITEM;

    // shrinked cubic bounding box so that entities can collide
    static final AxisAlignedBB boundingBox = new AxisAlignedBB(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

    public BlockNanoFog(){
        super(Material.IRON, NAME);
        setLightOpacity(255);
        setBlockUnbreakable();
        setResistance(31337.0F);
        setCreativeTab(null); //dont show in creative tab
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
        TileEntityNanoFog te = getTE(world, pos);

        return te.isBuild() && super.shouldSideBeRendered(state, world, pos, side);
    }

    public static TileEntityNanoFog getTE(IBlockAccess world, BlockPos pos){
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityNanoFog ? (TileEntityNanoFog) te : null;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntityNanoFog();
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
                entity.attackEntityFrom(new DamageSource("opensecurity.nanofog"), te.getDamage());
        }

        if(te.getKnockback() > 0 && !canEntityPass(world, pos, entity)) {
            if (te.getTerminal().consumeKnockbackEnergy(te.getKnockback())) {
                Vec3i kbVec = BlockUtils.getFacingFromEntity(pos, entity);
                entity.addVelocity(kbVec.getX() * te.getKnockback(), kbVec.getY() * te.getKnockback(), kbVec.getZ() * te.getKnockback());
            }
        }
    }

    /* galacticraft integration */
    @Optional.Method(modid = "galacticraftcore")
    public boolean isSealed(World var1, BlockPos var2, EnumFacing var3){
        return true;
    }
}
