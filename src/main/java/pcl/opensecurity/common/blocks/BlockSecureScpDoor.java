package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.items.ItemSecureScpDoor;

public class BlockSecureScpDoor extends BlockSecureDoor {
    public static final String NAME = "scp_secure_door";
    public static BlockSecureScpDoor DEFAULTITEM;

    public BlockSecureScpDoor() {
        super(NAME);
    }

    @Override
    protected Item getItem() {
        return ItemSecureScpDoor.DEFAULTSTACK.getItem();
    }
}
