package pcl.opensecurity.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.ContentRegistry;
public class BlockSecurePrivateDoor extends BlockSecureDoor {

	public BlockSecurePrivateDoor(Material materialIn) {
		super(materialIn);
		setUnlocalizedName("private_secure_door");
		setHardness(.5f);
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
