package pcl.opensecurity.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.blocks.BlockSecureMagDoor;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;

import javax.annotation.Nonnull;

public class ItemSecureMagDoor extends ItemSecureDoor {
    public static ItemStack DEFAULTSTACK;

    public ItemSecureMagDoor() {
        super(BlockSecureMagDoor.DEFAULTITEM);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return BlockSecureMagDoor.DEFAULTITEM.getUnlocalizedName();
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);

        if(result.equals(EnumActionResult.SUCCESS)){
            TileEntity teLower = worldIn.getTileEntity(pos.add(0, 1, 0));
            if(teLower instanceof TileEntitySecureDoor){
                ((TileEntitySecureDoor) teLower).setOwner(player.getUniqueID());
                ((TileEntitySecureDoor) teLower).enableMagReader();
            }
        }

        return result;
    }
}
