package pcl.opensecurity.client;

import pcl.opensecurity.OpenSecurity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTab extends CreativeTabs {
    public CreativeTab(String unlocalizedName) { 
        super(unlocalizedName);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Item.getItemFromBlock(OpenSecurity.cardWriter);
    }
}