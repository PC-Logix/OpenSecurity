/**
 * 
 */
package pcl.opensecurity.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import pcl.opensecurity.tileentity.TileEntityMagReader;

/**
 * @author Caitlyn
 *
 */
public class MagCardContainer extends Container {

	public MagCardContainer(InventoryPlayer inventory, TileEntityMagReader icte) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
		return false;
	}

}
