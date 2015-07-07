package pcl.opensecurity.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import pcl.opensecurity.OpenSecurity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {
	public CreativeTab(String unlocalizedName) {
		super(unlocalizedName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return Item.getItemFromBlock(OpenSecurity.DataBlock);
	}
}