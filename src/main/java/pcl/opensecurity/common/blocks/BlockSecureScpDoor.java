package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.items.ItemSecurePrivateDoor;

public class BlockSecureScpDoor extends BlockSecureDoor {
    public static final String NAME = "scp_secure_door";
    public static BlockSecureScpDoor DEFAULTITEM;

    public BlockSecurePrivateDoor() {
        super(NAME);
    }

    @Override
    protected Item getItem() {
        return ItemSecureScpDoor.DEFAULTSTACK.getItem();
    }
}
