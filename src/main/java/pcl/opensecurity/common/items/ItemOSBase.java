package pcl.opensecurity.common.items;

import net.minecraft.item.Item;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

public abstract class ItemOSBase extends Item {

    ItemOSBase(String name) {
        setUnlocalizedName("opensecurity." + name);
        setRegistryName(OpenSecurity.MODID, name);
        setCreativeTab(ContentRegistry.creativeTab);
    }
}
