/**
 * 
 */
package pcl.opensecurity.gui;

import pcl.opensecurity.containers.RFIDWriterContainer;
import pcl.opensecurity.tileentity.TileEntityRFIDWriter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * @author Caitlyn
 *
 */
public class OSGUIHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    	if (id == 0) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityRFIDWriter){
                    return new RFIDWriterContainer(player.inventory, (TileEntityRFIDWriter) tileEntity);
            }
    	}
    	return null;
	}


    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    	if (id == 0) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if(tileEntity instanceof TileEntityRFIDWriter){
                    return new RFIDWriterGUI(player.inventory, (TileEntityRFIDWriter) tileEntity);
            }
    	}
    	return null;
    }

}
