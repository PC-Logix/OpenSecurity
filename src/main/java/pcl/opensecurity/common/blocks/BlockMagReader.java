package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.tileentity.TileEntityMagReader;
import pcl.opensecurity.common.interfaces.IVariant;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class BlockMagReader extends Block implements ITileEntityProvider {
    public static final String NAME = "mag_reader";
    public static Block DEFAULTITEM;

    public BlockMagReader() {
        super(Material.IRON);
        setUnlocalizedName(NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    public static final IProperty<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
        return super.getPickBlock(getDefaultState(), target, world, pos, player);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMeta();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMagReader();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        //player.sendMessage(new TextComponentString("meta " + getMetaFromState(state)));
        world.scheduleBlockUpdate(pos, this, 20, 1);
        ItemStack heldItem = player.getHeldItemMainhand();
        if (!heldItem.isEmpty()) {
            Item equipped = heldItem.getItem();
            TileEntityMagReader tile = (TileEntityMagReader) world.getTileEntity(pos);
            if (!world.isRemote && equipped instanceof ItemMagCard) {
                world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ACTIVE));
                if (tile.doRead(heldItem, player, side)) {
                    world.setBlockState(pos, state.withProperty(VARIANT, EnumType.SUCCESS));
                } else {
                    world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ERROR));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.IDLE));
    }

    public enum EnumType implements IVariant {
        IDLE(0, "idle"),
        ACTIVE(1, "active"),
        SUCCESS(2, "success"),
        ERROR(3, "error");

        private static final EnumType[] META_LOOKUP = Stream.of(values()).sorted(Comparator.comparing(EnumType::getMeta)).toArray(EnumType[]::new);

        private final int meta;
        private final String name;

        EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name;
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static String[] getNames() {
            return Stream.of(META_LOOKUP).map(EnumType::getName).toArray(String[]::new);
        }
    }
}
