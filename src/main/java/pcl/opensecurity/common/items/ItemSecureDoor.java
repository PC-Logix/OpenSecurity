package pcl.opensecurity.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

import javax.annotation.Nonnull;

public class ItemSecureDoor extends ItemDoor {

    private Block block;

    public ItemSecureDoor() {
        this(ContentRegistry.secureDoor);
    }

    @SuppressWarnings("ConstantConditions")
    ItemSecureDoor(Block block) {
        super(block);
        this.block = block;

        setRegistryName(block.getRegistryName());
        setCreativeTab(ContentRegistry.creativeTab);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return block.getUnlocalizedName();
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedName();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

        if(result.equals(EnumActionResult.SUCCESS)){
            TileEntity teLower = worldIn.getTileEntity(pos.add(0, 1, 0));
            if(teLower instanceof TileEntitySecureDoor){
                ((TileEntitySecureDoor) teLower).setOwner(player.getUniqueID());
            }
        }

        return result;
    }

}