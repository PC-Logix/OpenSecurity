package pcl.opensecurity.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author Caitlyn
 *
 */
public class ItemRFIDCard extends Item {
	public ItemRFIDCard() {
		super();
		setUnlocalizedName("rfidCard");
		setTextureName("opensecurity:rfidCard");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int par2)
	{
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("color")) {
			return stack.getTagCompound().getInteger("color");	
		} else {
			return 0xFFFFFF;
		}
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity) {
		if (!itemStack.hasTagCompound() || !itemStack.stackTagCompound.hasKey("data")) {
			return super.itemInteractionForEntity(itemStack, player, entity);
		} else if (entity instanceof EntityLiving || entity instanceof EntityPlayer) {
			NBTTagCompound entityData = entity.getEntityData();
			NBTTagCompound rfidData;
			if (!entityData.hasKey("rfidData")) {
				rfidData = new NBTTagCompound();
				entityData.setTag("rfidData", rfidData);
			} else {
				rfidData = entityData.getCompoundTag("rfidData");
			}

			rfidData.setString("data", itemStack.stackTagCompound.getString("data"));
			rfidData.setString("uuid", itemStack.stackTagCompound.getString("uuid"));
			--itemStack.stackSize;
			return true;
		} else {
			return super.itemInteractionForEntity(itemStack, player, entity);
		}
	}
}
