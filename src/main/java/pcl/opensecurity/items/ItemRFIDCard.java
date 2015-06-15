package pcl.opensecurity.items;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity)
	{
		if (!itemStack.stackTagCompound.hasKey("data"))
		{
			return false;
		}
		else if (entity instanceof EntityLiving && !(entity instanceof EntityPlayerMP))
		{
			EntityLiving entityliving = (EntityLiving)entity;
			NBTTagCompound tag = entityliving.getEntityData().getCompoundTag("rfidData");
			tag.setString("data", itemStack.stackTagCompound.getString("data"));
			--itemStack.stackSize;
			return true;
		}
		else
		{
			return super.itemInteractionForEntity(itemStack, player, entity);
		}
	}
}
