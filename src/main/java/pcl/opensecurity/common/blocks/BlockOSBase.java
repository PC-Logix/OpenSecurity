package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

import java.util.Random;

/* implements facing blockstate */
public abstract class BlockOSBase extends Block implements ITileEntityProvider {
    protected Random random;

    public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    protected BlockOSBase(String name, Material materialIn, float hardness) {
        super(materialIn);
        setUnlocalizedName("opensecurity." + name);
        setRegistryName(OpenSecurity.MODID, name);
        setHardness(hardness);
        setCreativeTab(ContentRegistry.creativeTab);

        random = new Random();
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return getDefaultState().withProperty(PROPERTYFACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int facingbits = getFacing(state).getHorizontalIndex();
        return facingbits;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTYFACING);
    }

    @Override
    @Deprecated
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.fromAngle(placer.rotationYaw);
        return getDefaultState().withProperty(PROPERTYFACING, enumfacing);
    }

    public static EnumFacing getFacing(IBlockState state){
        return state.getValue(PROPERTYFACING);
    }
}
