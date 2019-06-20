package pcl.opensecurity.manual;


import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author Vexatos, ben-mkiv
 */
public abstract class ManualPathProvider {
    @Nullable
    public String pathFor(ItemStack stack) {
        if(stack == null) return null;

        if(stack.getItem() instanceof IItemWithDocumentation) {
            return ((IItemWithDocumentation) stack.getItem()).getDocumentationName(stack);
        }
        if(stack.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(stack.getItem());
            if(block instanceof IBlockWithDocumentation) {
                return ((IBlockWithDocumentation) block).getDocumentationName(stack);
            }
        }
        return null;
    }

    @Nullable
    public String pathFor(World world, BlockPos pos) {
        if(world == null) return null;

        Block block = world.getBlockState(pos).getBlock();
        if(block instanceof IBlockWithDocumentation) {
            return ((IBlockWithDocumentation) block).getDocumentationName(world, pos);
        }
        return null;
    }

}