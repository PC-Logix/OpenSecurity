package pcl.opensecurity.common.items;

import net.minecraft.item.ItemStack;
import pcl.opensecurity.common.blocks.BlockSecurePrivateDoor;

import javax.annotation.Nonnull;

public class ItemSecureScpDoor extends ItemSecureDoor {
    public static ItemStack DEFAULTSTACK;

    public ItemSecurePrivateDoor() {
        super(BlockSecureScpDoor.DEFAULTITEM);
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return BlockSecureScpDoor.DEFAULTITEM.getUnlocalizedName();
    }
}
