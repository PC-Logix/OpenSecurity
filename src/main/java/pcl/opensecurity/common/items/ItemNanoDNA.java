package pcl.opensecurity.common.items;

import net.minecraft.item.Item;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.ContentRegistry;

import static pcl.opensecurity.common.Reference.Names.ITEM_NANODNA;

public class ItemNanoDNA extends Item {
    public ItemNanoDNA() {
        setUnlocalizedName(ITEM_NANODNA);
        setRegistryName(OpenSecurity.MODID, ITEM_NANODNA);
        setCreativeTab(ContentRegistry.creativeTab);
    }
}