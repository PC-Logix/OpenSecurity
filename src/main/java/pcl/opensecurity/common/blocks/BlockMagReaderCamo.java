package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.interfaces.ICamo;
import pcl.opensecurity.common.interfaces.IOwner;
import pcl.opensecurity.common.items.ItemMagCard;
import pcl.opensecurity.common.tileentity.TileEntityMagReader;

public class BlockMagReaderCamo extends BlockCamouflage implements ITileEntityProvider {
    public static final String NAME = "mag_reader_camo";
    public static BlockMagReaderCamo DEFAULTITEM;

    public BlockMagReaderCamo() {
        this(NAME);
    }

    public BlockMagReaderCamo(String name) {
        super(Material.IRON, name);
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
    }

    // used by the renderer to control lighting and visibility of other blocks.
    // set to true because this block is opaque and occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    @Deprecated
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return false;
    }

    // used by the renderer to control lighting and visibility of other blocks, also by
    // (eg) wall or fence to control whether the fence joins itself to this block
    // set to true because this block occupies the entire 1x1x1 space
    // not strictly required because the default (super method) is true
    @Override
    @Deprecated
    public boolean isFullCube(IBlockState iBlockState) {
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
        //return super.getPickBlock(getDefaultState(), target, world, pos, player);
        return new ItemStack(Item.getItemFromBlock(this), 1, 0);
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

            world.scheduleBlockUpdate(pos, this, 20, 1);
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!heldItem.isEmpty()) {
                Item equipped = heldItem.getItem();
                TileEntityMagReader tile = (TileEntityMagReader) world.getTileEntity(pos);
                if (!world.isRemote) {
                    if (equipped instanceof ItemMagCard) {
                        //world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ACTIVE));
                        if (tile.doRead(heldItem, player, side)) {
                            //world.setBlockState(pos, state.withProperty(VARIANT, EnumType.SUCCESS));
                        } else {
                            //world.setBlockState(pos, state.withProperty(VARIANT, EnumType.ERROR));
                        }
                    }
                }
                super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
                return true;
            }
        return false;
    }

}
