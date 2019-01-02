package pcl.opensecurity.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockUtils {
    //from McJty Lib
    public static IBlockState placeStackAt(EntityPlayer player, ItemStack blockStack, World world, BlockPos pos, @Nullable IBlockState origState) {
        if (blockStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock) blockStack.getItem();
            if (origState == null) {
                origState = itemBlock.getBlock().getStateForPlacement(world, pos, EnumFacing.UP, 0, 0, 0, blockStack.getItem().getMetadata(blockStack), player, EnumHand.MAIN_HAND);
            }
            if (itemBlock.placeBlockAt(blockStack, player, world, pos, EnumFacing.UP, 0, 0, 0, origState)) {
                blockStack.shrink(1);
            }
            return origState;
        } else {
            player.setHeldItem(EnumHand.MAIN_HAND, blockStack);
            player.setPosition(pos.getX()+.5, pos.getY()+1.5, pos.getZ()+.5);
            blockStack.getItem().onItemUse(player, world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0, 0, 0);
            return world.getBlockState(pos);
        }
    }

    public static Vec3i getFacingFromEntity(BlockPos pos, Entity entity){
        Vec3d blockpos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vec3d entitypos = new Vec3d(entity.posX, entity.posY, entity.posZ);
        Vec3d offset = entitypos.subtract(blockpos);
        EnumFacing facing = EnumFacing.getFacingFromVector((float) offset.x, (float) offset.y, (float) offset.z);

        return facing.getDirectionVec();
    }
}
