/**
 * 
 */
package pcl.opensecurity;

import pcl.opensecurity.containers.MagCardContainer;
import pcl.opensecurity.containers.RFIDCardContainer;
import pcl.opensecurity.tileentity.MagComponent;
import pcl.opensecurity.tileentity.RFIDComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * @author Caitlyn
 *
 */
public class CommonProxy implements IGuiHandler {

	public void registerRenderers() {}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof RFIDComponent)
        {
        	RFIDComponent icte = (RFIDComponent) te;
            return new RFIDCardContainer(player.inventory, icte);
        }
        else
        {
            return null;
        }
	}
}
