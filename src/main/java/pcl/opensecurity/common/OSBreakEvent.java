package pcl.opensecurity.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityAlarm;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pcl.opensecurity.common.tileentity.TileEntitySecurityTerminal;

public class OSBreakEvent {
	
	public OSBreakEvent() {
		OpenSecurity.logger.info("Registering BreakEvent");
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onBlockBreak(BreakEvent event) {
		if (OpenSecurity.registerBlockBreakEvent) {
			TileEntity TE = event.getWorld().getTileEntity(event.getPos());
			if(TE instanceof TileEntitySecureDoor){
				TileEntitySecureDoor xEntity = (TileEntitySecureDoor) TE;
				if(xEntity.getOwner()!=null && !xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode && !xEntity.getOwner().isEmpty()){
					if (!event.getPlayer().canUseCommand(2,"")) {
						event.setCanceled(true);
					}
				}
			} else if(TE instanceof TileEntityDoorController){
				TileEntityDoorController xEntity = (TileEntityDoorController) TE;
				if(xEntity.getOwner()!=null && !xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode && !xEntity.getOwner().isEmpty()){
					if (!event.getPlayer().canUseCommand(2,"")) {
						event.setCanceled(true);
					}
				}
			}

            BlockPos eventPos = event.getPos();
            Iterable<BlockPos> blocks = BlockPos.getAllInBox(eventPos.add(-8.0f, -8.0f, -8.0f), eventPos.add(8.0f, 8.0f, 8.0f));

            for (BlockPos pos : blocks) {
                World world = event.getWorld();
                if(world.getTileEntity(pos) instanceof TileEntitySecurityTerminal){ // Not sure what to put here <<
                    TileEntitySecurityTerminal xEntity = (TileEntitySecurityTerminal) world.getTileEntity(pos);
                    if(xEntity.getOwner()!=null && !xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !event.getPlayer().capabilities.isCreativeMode && !xEntity.getOwner().isEmpty()){
                        if (!event.getPlayer().canUseCommand(2,"") && xEntity.isEnabled()) {
                            xEntity.usePower();
                            event.setCanceled(true);
                        }
                    }
                }
            }

		}
	}
}