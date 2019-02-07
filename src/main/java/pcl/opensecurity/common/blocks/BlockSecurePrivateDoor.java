package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.ContentRegistry;

public class BlockSecurePrivateDoor extends BlockSecureDoor {
    public static final String NAME = "private_secure_door";

    public BlockSecurePrivateDoor() {
        super(NAME);
    }

    @Override
    protected Item getItem() {
        return ContentRegistry.securePrivateDoorItem;
    }
}
