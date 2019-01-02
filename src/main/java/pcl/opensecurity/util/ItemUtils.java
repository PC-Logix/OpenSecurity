package pcl.opensecurity.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemUtils {
    public static void dropItem(ItemStack stack, World world, BlockPos pos, boolean motion, int pickupDelay){
        if(world.isRemote) return;

        if(stack.getMaxStackSize() <= 0 || stack.isEmpty())
            return;

        EntityItem entityitem = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        entityitem.setPickupDelay(pickupDelay);
        if(!motion){
            entityitem.motionX=0;
            entityitem.motionY=0;
            entityitem.motionZ=0;
        }

        world.spawnEntity(entityitem);
    }
}
