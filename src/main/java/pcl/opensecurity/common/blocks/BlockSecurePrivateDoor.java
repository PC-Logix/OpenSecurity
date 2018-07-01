package pcl.opensecurity.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.Reference;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSecurePrivateDoor extends BlockSecureDoor {

    public BlockSecurePrivateDoor() {
        super(Reference.Names.BLOCK_PRIVATE_SECURE_DOOR);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockSecureDoor.EnumDoorHalf.UPPER ? null : this.getItem();
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this.getItem());
    }

    private Item getItem() {
        return new ItemStack(ContentRegistry.privateSecureDoor).getItem();
    }
}
