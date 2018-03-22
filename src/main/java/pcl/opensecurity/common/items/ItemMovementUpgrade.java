package pcl.opensecurity.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMovementUpgrade extends Item {

	public ItemMovementUpgrade() {
		super();
		setUnlocalizedName("movementUpgrade");
	}
	
	public boolean hasOverlay(ItemStack stack) {
		return getColor(stack) != 0x00FFFFFF;
	}

	/**
	 * Return whether the specified card ItemStack has a color.
	 */
	public boolean hasColor(ItemStack stack) {
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		return (nbttagcompound != null && nbttagcompound.hasKey("display", 10)) && nbttagcompound.getCompoundTag("display").hasKey("color", 3);
	}

	/**
	 * Return the color for the specified card ItemStack.
	 */
	public int getColor(ItemStack stack) {
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound != null)
		{
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

			if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3))
			{
				return nbttagcompound1.getInteger("color");
			}
		}

		return 0xFFFFFF;

	}

	public void setColor(ItemStack stack, int color) {
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound == null)
		{
			nbttagcompound = new NBTTagCompound();
			stack.setTagCompound(nbttagcompound);
		}

		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

		if (!nbttagcompound.hasKey("display", 10))
		{
			nbttagcompound.setTag("display", nbttagcompound1);
		}

		nbttagcompound1.setInteger("color", color);
	}

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	return EnumActionResult.SUCCESS;
    }

}
