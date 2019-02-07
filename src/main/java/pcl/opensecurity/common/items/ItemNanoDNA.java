package pcl.opensecurity.common.items;

import net.minecraft.item.Item;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

public class ItemNanoDNA extends Item {
    public static final String NAME = "nanodna";

    public ItemNanoDNA() {
        setUnlocalizedName(NAME);
        setRegistryName(OpenSecurity.MODID, NAME);
        setCreativeTab(ContentRegistry.creativeTab);
    }
}