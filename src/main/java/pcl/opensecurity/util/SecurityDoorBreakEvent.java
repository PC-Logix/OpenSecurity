package pcl.opensecurity.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import pcl.opensecurity.tileentity.TileEntityDoorController;
import pcl.opensecurity.tileentity.TileEntitySecureDoor;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SecurityDoorBreakEvent {
	
	public SecurityDoorBreakEvent() {
		System.out.println("Registering Event");
	}
	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onBlockBreak(BreakEvent event) {
		TileEntity entity = event.world.getTileEntity(event.x, event.y, event.z);
		if(entity instanceof TileEntitySecureDoor){
			TileEntitySecureDoor xEntity = (TileEntitySecureDoor) entity;
			if(xEntity.getOwner()!=null){
				System.out.println(xEntity.getOwner().toString().equals(event.getPlayer().getUniqueID().toString()));
				if(!xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode) {
					event.setCanceled(true);
				}
			}
		} else if(entity instanceof TileEntityDoorController){
			TileEntityDoorController xEntity = (TileEntityDoorController) entity;
			if(xEntity.getOwner()!=null){
				System.out.println(xEntity.getOwner().toString().equals(event.getPlayer().getUniqueID().toString()));
				if(!xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode) {
					event.setCanceled(true);
				}
			}
		}
		
	}
}
