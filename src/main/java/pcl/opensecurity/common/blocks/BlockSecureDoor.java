package pcl.opensecurity.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.items.ItemSecureDoor;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static pcl.opensecurity.common.protection.Protection.UserAction.mine;

@SuppressWarnings("deprecation")
public class BlockSecureDoor extends BlockDoor {
    public static final String NAME = "secure_door";
    public static BlockSecureDoor DEFAULTITEM;

    public BlockSecureDoor() {
        this(NAME);
    }

    BlockSecureDoor(String name) {
        super(Material.IRON);
        setRegistryName(OpenSecurity.MODID, name);
        setUnlocalizedName(name);
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Nonnull
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(getItem());
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    protected Item getItem() {
        return ItemSecureDoor.DEFAULTSTACK.getItem();
    }

    @Nonnull
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return blockMapColor;
    }

    @Nonnull
    public String getLocalizedName() {
        // Copied from block class in order to override the superclass' behavior
        return new TextComponentTranslation(this.getUnlocalizedName() + ".name").getUnformattedText();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new TileEntitySecureDoor();
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest){
        if(!player.isCreative()) {
            if (Protection.isProtected(player, mine, pos))
                return false;

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntitySecureDoor) {
                if (!((TileEntitySecureDoor) te).getOwner().equals(player.getUniqueID()))
                    return false;
            }
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    public static BlockPos getOtherDoorPart(World world, BlockPos thisPos) {
        if (world.getTileEntity(new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ()))  instanceof TileEntitySecureDoor){
            return new BlockPos(thisPos.getX(), thisPos.getY() + 1, thisPos.getZ());
        } else {
            return new BlockPos(thisPos.getX(), thisPos.getY() - 1, thisPos.getZ());
        }
    }


    //vanilla method without redstone...
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
        if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            BlockPos blockpos = pos.down();
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() != this)
                worldIn.setBlockToAir(pos);
            else if (blockIn != this)
                iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
        }
        else {
            boolean flag1 = false;
            BlockPos blockpos1 = pos.up();
            IBlockState iblockstate1 = worldIn.getBlockState(blockpos1);

            if (iblockstate1.getBlock() != this){
                worldIn.setBlockToAir(pos);
                flag1 = true;
            }

            if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn,  pos.down(), EnumFacing.UP)) {
                worldIn.setBlockToAir(pos);
                flag1 = true;

                if (iblockstate1.getBlock() == this)
                    worldIn.setBlockToAir(blockpos1);
            }

            if (flag1 && !worldIn.isRemote)
                this.dropBlockAsItem(worldIn, pos, state, 0);

        }
    }


}
