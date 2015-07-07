package pcl.opensecurity.items;

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
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity) {
		if (!itemStack.stackTagCompound.hasKey("data")) {
			return false;
		} else if (entity instanceof EntityLiving) {
			NBTTagCompound entityData = entity.getEntityData();
			NBTTagCompound rfidData;
			if (!entityData.hasKey("rfidData")) {
				entityData.setTag("rfidData", (rfidData = new NBTTagCompound()));
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
