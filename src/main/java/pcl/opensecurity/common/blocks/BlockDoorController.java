package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.items.ItemSecureDoor;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;
import pcl.opensecurity.util.IOwner;

public class BlockDoorController extends BlockCamouflage implements ITileEntityProvider  {
    public static final String NAME = "door_controller";

    public BlockDoorController() {
        this(NAME);
    }

    public BlockDoorController(String name) {
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty())
            return true;

        Block block = Block.getBlockFromItem(heldItem.getItem());
        int meta = heldItem.getMetadata();

        if (block.isFullCube(block.getDefaultState()) || block instanceof BlockGlass || block instanceof BlockStainedGlass) {
            TileEntityDoorController tileEntity = (TileEntityDoorController) world.getTileEntity(pos);
            if (tileEntity == null || player.isSneaking() || heldItem.getItem() instanceof ItemDoor || heldItem.getItem() instanceof ItemSecureDoor) {
                return false;
            }

            //If the user is not the owner, or the user is not in creative drop out.
            if (tileEntity.getOwner() != null) {
                if (!tileEntity.getOwner().equals(player.getUniqueID()) && !player.capabilities.isCreativeMode) {
                    if (tileEntity.getOwner() != null) {
                        return true;
                    }
                }
            }
            if ((tileEntity.getOwner() != null && tileEntity.getOwner().equals(player.getUniqueID())) || player.capabilities.isCreativeMode) {
                if (heldItem.getItem() instanceof ItemBlock) {
                    tileEntity.setCamoBlock(block, meta);
                    return true;

                }
            }
        }
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new TileEntityDoorController();
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


}
