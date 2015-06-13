package pcl.opensecurity.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class ItemRFIDCard extends Item {
	public ItemRFIDCard() {
		super();
		setUnlocalizedName("rfidCard");
		setTextureName("opensecurity:rfidCard");
	}
}
