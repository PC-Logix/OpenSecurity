package pcl.opensecurity.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author Caitlyn
 *
 */
public class ItemEnergyUpgrade extends Item {

	public ItemEnergyUpgrade() {
		super();
		setUnlocalizedName("energyUpgrade");
		setTextureName("opensecurity:energyUpgrade");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int par2)
	{
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("color")) {
			return stack.getTagCompound().getInteger("color");	
		} else {
			return 0xFFFFFF;
		}
	}
}
