package pcl.opensecurity.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.tileentity.TileEntityDoorController;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class OSBreakEvent {
	
	public OSBreakEvent() {
		OpenSecurity.logger.info("Registering BreakEvent");
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onBlockBreak(BreakEvent event) {
		TileEntity TE = event.world.getTileEntity(event.x, event.y, event.z);
		if(TE instanceof TileEntitySecureDoor){
			TileEntitySecureDoor xEntity = (TileEntitySecureDoor) TE;
			if(xEntity.getOwner()!=null){
				if(!xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode) {
					if(!xEntity.getOwner().isEmpty()) {
						event.setCanceled(true);						
					}
				}
			}
		} else if(TE instanceof TileEntityDoorController){
			TileEntityDoorController xEntity = (TileEntityDoorController) TE;
			if(xEntity.getOwner()!=null){
				if(!xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode) {
					if(!xEntity.getOwner().isEmpty()) {
						event.setCanceled(true);						
					}
				}
			}
		}		
	}
}
