package pcl.opensecurity.common.items;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.opensecurity.OpenSecurity;

public class ItemSHFRFIDCard extends ItemOSBase {
    public static final String NAME = "shfrfid_card";
    public static ItemStack DEFAULTSTACK;

    public ItemSHFRFIDCard() {
        super(NAME);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.SUCCESS;
    }

    public static class CardTag {
        public boolean locked = false;
        public String dataTag = "";
        public int color = 0xFFFFFF;

        public boolean isValid;

        public CardTag(ItemStack stack) {
            if (stack.getItem() instanceof ItemCard)
                readFromNBT(stack.getTagCompound());
        }

        public CardTag(NBTTagCompound nbt) {
            readFromNBT(nbt);
        }

        public void readFromNBT(NBTTagCompound nbt) {
            if (nbt != null) {

                if (nbt.hasKey("data"))
                    dataTag = nbt.getString("data");

                if (nbt.hasKey("locked"))
                    locked = nbt.getBoolean("locked");

                if (nbt.hasKey("display", 10)) {
                    NBTTagCompound displayTag = nbt.getCompoundTag("display");
                    if (displayTag.hasKey("color", 3)) {
                        color = displayTag.getInteger("color");
                    }
                }
            }

            isValid = dataTag.length() > 0;
        }

        public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("data")) {
                return itemInteractionForEntity(stack, playerIn, target, hand);
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
                stack.setCount((stack.getCount() - 1));
                return true;
            } else {
                return itemInteractionForEntity(stack, playerIn, target, hand);
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            nbt.setString("data", dataTag);
            nbt.setBoolean("locked", locked);

            NBTTagCompound displayTag = new NBTTagCompound();
            displayTag.setInteger("color", color);

            nbt.setTag("display", displayTag);

            return nbt;
        }

    }
}
