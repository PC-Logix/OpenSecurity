package pcl.opensecurity.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import pcl.opensecurity.tileentity.TileEntityKVM;

public class KVMContainer extends Container {
	protected TileEntityKVM tileEntity;

	public KVMContainer(InventoryPlayer inventoryPlayer, TileEntityKVM te) {
		tileEntity = te;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		if (player == null) {
			return false;
		} else if (tileEntity == null) {
			return false;
		} else
			return tileEntity.isUseableByPlayer(player);
	}
	
}