package pcl.opensecurity.common.blocks;

import net.minecraft.item.Item;
import pcl.opensecurity.common.items.ItemSecureMagDoor;
import pcl.opensecurity.common.items.ItemSecurePrivateDoor;

public class BlockSecureMagDoor extends BlockSecureDoor {
    public static final String NAME = "mag_secure_door";
    public static BlockSecureMagDoor DEFAULTITEM;

    public BlockSecureMagDoor() {
        super(NAME);
    }

    @Override
    protected Item getItem() {
        return ItemSecureMagDoor.DEFAULTSTACK.getItem();
    }
}
