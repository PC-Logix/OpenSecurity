package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.opensecurity.common.UnlistedPropertyCopiedBlock;
import pcl.opensecurity.common.items.ItemSecureDoor;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;
import pcl.opensecurity.util.ICamo;
import pcl.opensecurity.util.IOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BlockDoorController extends BlockOSBase {
    public static final String NAME = "door_controller";

    public BlockDoorController() {
        this(NAME);
    }

    public BlockDoorController(String name) {
        super(NAME, Material.IRON, 0.5f);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
        TileEntityDoorController te = (TileEntityDoorController) world.getTileEntity(pos);
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        ((IOwner) te).setOwner(placer.getUniqueID());
    }

    @Override
    @Deprecated
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing enumfacing = (placer == null) ? EnumFacing.NORTH : EnumFacing.fromAngle(placer.rotationYaw);
        return this.getDefaultState().withProperty(PROPERTYFACING, enumfacing);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty())
            return true;

        Block block = Block.getBlockFromItem(heldItem.getItem());
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
                    tileEntity.overrideTexture(heldItem);
                    world.scheduleUpdate(pos, this, 1);
                    world.notifyBlockUpdate(pos, this.getDefaultState(), this.getDefaultState(), 3);
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


    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
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

    // render using an IBakedModel
    // not strictly required because the default (super method) is MODEL.
    @Override
    @Deprecated
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.MODEL;
    }

    // createBlockState is used to define which properties your blocks possess
    // Vanilla BlockState is composed of listed properties only.  A variant is created for each combination of listed
    //   properties; for example two properties ON(true/false) and READY(true/false) would give rise to four variants
    //   [on=true, ready=true]
    //   [on=false, ready=true]
    //   [on=true, ready=false]
    //   [on=false, ready=false]
    // Forge adds ExtendedBlockState, which has two types of property:
    // - listed properties (like vanilla), and
    // - unlisted properties, which can be used to convey information but do not cause extra variants to be created.
    @Override
    protected BlockStateContainer createBlockState() {
        IProperty[] listedProperties = new IProperty[]{PROPERTYFACING}; // no listed properties
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[]{COPIEDBLOCK};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    }

    // this method uses the block state and BlockPos to update the unlisted COPIEDBLOCK property in IExtendedBlockState based
    // on non-metadata information.  This is then conveyed to the IBakedModel#getQuads during rendering.
    // In this case, we look around the camouflage block to find a suitable adjacent block it should camouflage itself as
    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state instanceof IExtendedBlockState) {  // avoid crash in case of mismatch
            IExtendedBlockState retval = (IExtendedBlockState) state;
            IBlockState bestAdjacentBlock = getCamoFromNBT(world, pos);
            retval = retval.withProperty(COPIEDBLOCK, bestAdjacentBlock);
            return retval;
        }
        return state;
    }

    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state;  //for debugging - useful spot for a breakpoint.  Not necessary.
    }

    // the COPIEDBLOCK property is used to store the identity of the block that BlockCamouflage will copy
    public static final UnlistedPropertyCopiedBlock COPIEDBLOCK = new UnlistedPropertyCopiedBlock();

    // Select the best adjacent block to camouflage as.
    // Algorithm is:
    // 1) Ignore any blocks which are not solid (CUTOUTS or TRANSLUCENT).  Ignore adjacent camouflage.
    // 2) If there are more than one type of solid block, choose the type which is present on the greatest number of sides
    // 3) In case of a tie, prefer the type which span opposite sides of the blockpos, for example:
    //       up and down; east and west; north and south.
    // 4) If still a tie, look again for spans on both sides, counting adjacent camouflage blocks as a span
    // 5) If still a tie, in decreasing order of preference: NORTH, SOUTH, EAST, WEST, DOWN, UP
    // 6) If no suitable adjacent blocks, return Block.air
    @Deprecated
    private IBlockState getCamoFromNBT(IBlockAccess world, BlockPos blockPos) {
        ICamo te = (ICamo) world.getTileEntity(blockPos);
        final IBlockState UNCAMOUFLAGED_BLOCK = Blocks.AIR.getDefaultState();
        TreeMap<EnumFacing, IBlockState> adjacentSolidBlocks = new TreeMap<EnumFacing, IBlockState>();

        HashMap<IBlockState, Integer> adjacentBlockCount = new HashMap<IBlockState, Integer>();
        for (EnumFacing facing : EnumFacing.values()) {
            IBlockState camoBlockState = te.getBlockFromNBT();
            Block camoBlock = camoBlockState.getBlock();
            if (camoBlock != Blocks.AIR) {
                adjacentSolidBlocks.put(facing, camoBlockState);
                if (adjacentBlockCount.containsKey(camoBlockState)) {
                    adjacentBlockCount.put(camoBlockState, 1 + adjacentBlockCount.get(camoBlockState));
                } else if (camoBlockState.getBlock() != this) {
                    adjacentBlockCount.put(camoBlockState, 1);
                }
            }
        }

        if (adjacentBlockCount.isEmpty()) {
            return UNCAMOUFLAGED_BLOCK;
        }

        if (adjacentSolidBlocks.size() == 1) {
            IBlockState singleAdjacentBlock = adjacentSolidBlocks.firstEntry().getValue();
            if (singleAdjacentBlock.getBlock() == this) {
                return UNCAMOUFLAGED_BLOCK;
            } else {
                return singleAdjacentBlock;
            }
        }

        int maxCount = 0;
        ArrayList<IBlockState> maxCountIBlockStates = new ArrayList<IBlockState>();
        for (Map.Entry<IBlockState, Integer> entry : adjacentBlockCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCountIBlockStates.clear();
                maxCountIBlockStates.add(entry.getKey());
                maxCount = entry.getValue();
            } else if (entry.getValue() == maxCount) {
                maxCountIBlockStates.add(entry.getKey());
            }
        }

        assert maxCountIBlockStates.isEmpty() == false;
        if (maxCountIBlockStates.size() == 1) {
            return maxCountIBlockStates.get(0);
        }

        // for each block which has a match on the opposite side, add 10 to its count.
        // exact matches are counted twice --> +20, match with BlockCamouflage only counted once -> +10
        for (Map.Entry<EnumFacing, IBlockState> entry : adjacentSolidBlocks.entrySet()) {
            IBlockState iBlockState = entry.getValue();
            if (maxCountIBlockStates.contains(iBlockState)) {
                EnumFacing oppositeSide = entry.getKey().getOpposite();
                IBlockState oppositeBlock = adjacentSolidBlocks.get(oppositeSide);
                if (oppositeBlock != null && (oppositeBlock == iBlockState || oppositeBlock.getBlock() == this)) {
                    adjacentBlockCount.put(iBlockState, 10 + adjacentBlockCount.get(iBlockState));
                }
            }
        }

        maxCount = 0;
        maxCountIBlockStates.clear();
        for (Map.Entry<IBlockState, Integer> entry : adjacentBlockCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCountIBlockStates.clear();
                maxCountIBlockStates.add(entry.getKey());
                maxCount = entry.getValue();
            } else if (entry.getValue() == maxCount) {
                maxCountIBlockStates.add(entry.getKey());
            }
        }
        assert maxCountIBlockStates.isEmpty() == false;
        if (maxCountIBlockStates.size() == 1) {
            return maxCountIBlockStates.get(0);
        }

        EnumFacing[] orderOfPreference = new EnumFacing[]{EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST,
                EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.UP};

        for (EnumFacing testFace : orderOfPreference) {
            if (adjacentSolidBlocks.containsKey(testFace) &&
                    maxCountIBlockStates.contains(adjacentSolidBlocks.get(testFace))) {
                return adjacentSolidBlocks.get(testFace);
            }
        }
        assert false : "this shouldn't be possible";
        return null;
    }

}
