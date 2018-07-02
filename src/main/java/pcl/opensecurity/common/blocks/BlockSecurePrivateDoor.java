package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.ContentRegistry;
import pcl.opensecurity.common.Reference;

public class BlockSecurePrivateDoor extends BlockSecureDoor {

    public BlockSecurePrivateDoor() {
        super(Reference.Names.BLOCK_PRIVATE_SECURE_DOOR);
    }

    @Override
    protected Item getItem() {
        return ContentRegistry.securePrivateDoorItem;
    }
}
