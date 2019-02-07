package pcl.opensecurity.common.items;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

public class ItemRFIDCard extends ItemCard {
    public static final String NAME = "rfid_card";

    public ItemRFIDCard() {
        super(NAME);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("data")) {
            return super.itemInteractionForEntity(stack, playerIn, target, hand);
        } else if (target instanceof EntityLiving || target instanceof EntityPlayer) {
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
            stack.setCount((stack.getCount() - 1));
            return true;
        } else {
            return super.itemInteractionForEntity(stack, playerIn, target, hand);
        }
    }

}
