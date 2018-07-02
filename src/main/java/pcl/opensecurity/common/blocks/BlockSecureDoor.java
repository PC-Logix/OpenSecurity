package pcl.opensecurity.common.blocks;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.Reference;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockSecureDoor extends BlockDoor {

    public BlockSecureDoor() {
        this(Reference.Names.BLOCK_SECURE_DOOR);
    }

    BlockSecureDoor(String name) {
        super(Material.IRON);
        setRegistryName(new ResourceLocation(OpenSecurity.MODID, name));
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
        return ContentRegistry.secureDoorItem;
    }

    @Nonnull
    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return blockMapColor;
    }

    @Nonnull
    public String getLocalizedName() {
        // Copied from block class in order to override the superclass' behavior
        return I18n.translateToLocal(this.getUnlocalizedName() + ".name");
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
}