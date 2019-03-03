package pcl.opensecurity.common.items;

import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.blocks.BlockSecurePrivateDoor;

import javax.annotation.Nonnull;

public class ItemSecurePrivateDoor extends ItemSecureDoor {
    public static ItemStack DEFAULTSTACK;

    public ItemSecurePrivateDoor() {
        super(BlockSecurePrivateDoor.DEFAULTITEM);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return BlockSecurePrivateDoor.DEFAULTITEM.getUnlocalizedName();
    }
}
