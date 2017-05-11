/**
 * 
 */
package pcl.opensecurity.common;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * @author Caitlyn
 *
 */
public class CommonProxy implements IGuiHandler {

	public World getWorld(int dimId) {
		//overridden separately for client and server.
		return null;
	}
	
	public void registerSounds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerItemRenderers() {
		// TODO Auto-generated method stub
		
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}
}
