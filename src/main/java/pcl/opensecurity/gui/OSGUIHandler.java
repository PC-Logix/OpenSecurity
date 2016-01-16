/**
 * 
 */
package pcl.opensecurity.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pcl.opensecurity.containers.CardWriterContainer;
import pcl.opensecurity.containers.EnergyTurretContainer;
import pcl.opensecurity.containers.KVMContainer;
import pcl.opensecurity.tileentity.TileEntityCardWriter;
import pcl.opensecurity.tileentity.TileEntityEnergyTurret;
import pcl.opensecurity.tileentity.TileEntityKVM;
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
			if (tileEntity instanceof TileEntityCardWriter) {
				return new CardWriterContainer(player.inventory, (TileEntityCardWriter) tileEntity);
			}
		} else if (id == 1) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityKVM) {
				return new KVMContainer(player.inventory, (TileEntityKVM) tileEntity);
			}
			return null;
		} else if (id == 2) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityEnergyTurret) {
				return new EnergyTurretContainer(player.inventory, (TileEntityEnergyTurret) tileEntity);
			}
			return null;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityCardWriter) {
				return new CardWriterGUI(player.inventory, (TileEntityCardWriter) tileEntity);
			}
		} else if (id == 1) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityKVM) {
				return new KVMGUI(player.inventory, (TileEntityKVM) tileEntity);
			}
		} else if (id == 2) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if (tileEntity instanceof TileEntityEnergyTurret) {
				return new EnergyTurretGUI(player.inventory, (TileEntityEnergyTurret) tileEntity);
			}
		}
		return null;
	}

}
