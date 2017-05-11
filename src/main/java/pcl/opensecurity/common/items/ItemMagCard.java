package pcl.opensecurity.common.items;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMagCard extends ItemCard {
	public ItemMagCard() {
		super();
		setUnlocalizedName("itemMagCard");
	}

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    	setColor(stack, 0x00FF00);
    	return EnumActionResult.SUCCESS;
    }
	
	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (!stack.getTagCompound().hasKey("data")) {
			return false;
		} else if (target instanceof EntityLiving) {
			NBTTagCompound entityData = target.getEntityData();
			NBTTagCompound rfidData;
			if (!entityData.hasKey("rfidData")) {
				rfidData = new NBTTagCompound();
				entityData.setTag("rfidData", rfidData);
			} else {
				rfidData = entityData.getCompoundTag("rfidData");
			}

			rfidData.setString("data", stack.getTagCompound().getString("data"));
			rfidData.setString("uuid", stack.getTagCompound().getString("uuid"));
			--stack.stackSize;
			return true;
		} else {
			return super.itemInteractionForEntity(stack, playerIn, target, hand);
		}
	}

}