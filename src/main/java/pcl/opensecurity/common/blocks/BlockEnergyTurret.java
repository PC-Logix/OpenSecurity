package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntityEnergyTurret;

public class BlockEnergyTurret extends Block implements ITileEntityProvider {
    public static final String NAME = "energy_turret";
    public static final int GUI_ID = 2;
    static final AxisAlignedBB bbBottom = new AxisAlignedBB(0, 0, 0, 1, 1d/16 * 6, 1);

    public static final PropertyDirection PROPERTYPITCH = PropertyDirection.create("pitch", EnumFacing.Plane.VERTICAL);

    public BlockEnergyTurret() {
        super(Material.IRON);
        setUnlocalizedName(NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEnergyTurret();
    }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
        return bbBottom;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Deprecated
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PROPERTYPITCH);
    }

    @Override
    @Deprecated
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing enumfacing = blockFaceClickedOn.equals(EnumFacing.DOWN) ? EnumFacing.UP : EnumFacing.DOWN;
        return getDefaultState().withProperty(PROPERTYPITCH, enumfacing);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        // Only execute on the server
        if (world.isRemote) {
            return true;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityEnergyTurret)) {
            return false;
        }
        player.openGui(OpenSecurity.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public static EnumFacing getMount(IBlockState state){
        return state.getValue(PROPERTYPITCH);
    }

    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing pitch = meta == 0 ? EnumFacing.DOWN : EnumFacing.UP;
        return getDefaultState().withProperty(PROPERTYPITCH, pitch);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int pitchbits = getMount(state).equals(EnumFacing.DOWN) ? 0 : 1;
        return pitchbits;
    }

}
