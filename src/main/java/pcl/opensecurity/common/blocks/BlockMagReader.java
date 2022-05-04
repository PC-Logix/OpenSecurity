package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.interfaces.IOwner;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.tileentity.TileEntityMagReader;
import pcl.opensecurity.common.interfaces.IVariant;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Stream;

public class BlockMagReader extends Block implements ITileEntityProvider {
    public static final String NAME = "mag_reader";
    public static Block DEFAULTITEM;
    private int timer = 0;
    private int thisState = 0;
    private Boolean runningTick = false;

    public BlockMagReader() {
        super(Material.IRON);
        setUnlocalizedName("opensecurity." + NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setHardness(0.5f);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity te = worldIn.getTileEntity(pos);
        ((IOwner) te).setOwner(placer.getUniqueID());
        worldIn.scheduleBlockUpdate(pos, this, 1, 1);
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
        ItemStack heldItem;

        if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemMagCard) {
            heldItem = player.getHeldItemMainhand();
        } else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemMagCard) {
            heldItem = player.getHeldItemOffhand();
        } else {
            return false;
        }

        if (!heldItem.isEmpty()) {
            System.out.println(heldItem.getItem().getRegistryName().toString());
            Item equipped = heldItem.getItem();
            TileEntityMagReader tile = (TileEntityMagReader) world.getTileEntity(pos);

            if (!world.isRemote && equipped instanceof ItemMagCard) {
                if (tile.swipeInd) {
                    if (!runningTick)
                        world.scheduleBlockUpdate(pos, this, 1, 1);
                    timer = 20;
                    world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ACTIVE));
                    if (tile.doRead(heldItem, player, side) && tile.swipeInd) {
                        world.setBlockState(pos, state.withProperty(VARIANT, EnumType.SUCCESS));
                    } else {
                        world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ERROR));
                    }
                } else {
                    tile.doRead(heldItem, player, side);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        runningTick = true;
        TileEntityMagReader tile = (TileEntityMagReader) worldIn.getTileEntity(pos);
        if (tile.swipeInd) {
            if(timer == 0)
                worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.IDLE));
            else
                timer--;
        } else { //Simple way I solved it
            if (thisState != tile.doorState) {
                switch (tile.doorState) {
                    case 1:
                        worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.ERROR));
                        break;
                    case 2:
                        worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.ACTIVE));
                        break;
                    case 3:
                        worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.SUCCESS));
                        break;
                    default:
                        worldIn.setBlockState(pos, state.withProperty(VARIANT, EnumType.IDLE));
                        break;
                }
                thisState = tile.doorState;
            }
        }
        worldIn.scheduleBlockUpdate(pos, this, 1, 1);
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
