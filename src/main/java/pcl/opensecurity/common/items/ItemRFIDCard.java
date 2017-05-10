package pcl.opensecurity.common.items;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRFIDCard extends Item implements IItemColor {
	public ItemRFIDCard() {
		super();
		setUnlocalizedName("itemRFIDCard");
		setRegistryName("itemRFIDCard");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int tintIndex) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("color")) {
			return stack.getTagCompound().getInteger("color");	
		} else {
			return 0xFFFFFF;
		}
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