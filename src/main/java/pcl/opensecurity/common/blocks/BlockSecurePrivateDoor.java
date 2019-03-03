package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.items.ItemSecurePrivateDoor;

public class BlockSecurePrivateDoor extends BlockSecureDoor {
    public static final String NAME = "private_secure_door";
    public static BlockSecurePrivateDoor DEFAULTITEM;

    public BlockSecurePrivateDoor() {
        super(NAME);
    }

    @Override
    protected Item getItem() {
        return ItemSecurePrivateDoor.DEFAULTSTACK.getItem();
    }
}
