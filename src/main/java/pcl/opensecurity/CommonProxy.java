/**
 * 
 */
package pcl.opensecurity;

import pcl.opensecurity.containers.RFIDCardContainer;
import pcl.opensecurity.tileentity.TileEntityRFIDReader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author Caitlyn
 *
 */
public class CommonProxy implements IGuiHandler {

	public void registerRenderers() {
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityRFIDReader) {
			TileEntityRFIDReader icte = (TileEntityRFIDReader) te;
			return new RFIDCardContainer(player.inventory, icte);
		} else {
			return null;
		}
	}
}
