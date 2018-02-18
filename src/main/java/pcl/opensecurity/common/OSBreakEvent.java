package pcl.opensecurity.common;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.tileentity.TileEntityDoorController;
import pcl.opensecurity.common.tileentity.TileEntitySecureDoor;
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
				if(xEntity.getOwner()!=null && !xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !xEntity.getOwner().isEmpty()){
					if (!event.getPlayer().canUseCommand(2,"")) {
						event.setCanceled(true);
					}
				}
			} else if(TE instanceof TileEntityDoorController){
				TileEntityDoorController xEntity = (TileEntityDoorController) TE;
				if(xEntity.getOwner()!=null && !xEntity.getOwner().equals(event.getPlayer().getUniqueID().toString()) && !xEntity.getOwner().isEmpty()){
					if (!event.getPlayer().canUseCommand(2,"")) {
						event.setCanceled(true);
					}
				}
			}

            BlockPos eventPos = event.getPos();
            Iterable<BlockPos> blocks = BlockPos.getAllInBox(eventPos.add(-32.0f, -32.0f, -32.0f), eventPos.add(32.0f, 32.0f, 32.0f));
			World world = event.getWorld();
            long startTime = System.currentTimeMillis();
            for (BlockPos pos : blocks) {
                if(world.getTileEntity(pos) instanceof TileEntitySecurityTerminal){
                    TileEntitySecurityTerminal xEntity = (TileEntitySecurityTerminal) world.getTileEntity(pos);
                    if (getDistance(xEntity.rangeMod, event)) {
                        if(xEntity.getOwner()!=null && !xEntity.isUserAllowedToBypass(event.getPlayer().getUniqueID().toString()) && !xEntity.getOwner().isEmpty()){
                            if (!event.getPlayer().canUseCommand(2,"") && xEntity.isEnabled()) {
                                if (xEntity.usePower()) {
                                    event.setCanceled(true);
                                    event.getPlayer().sendMessage(new TextComponentString("Breaking blocks is not allowed as you are not the owner of this area."));
                                }
                            }
                        }
                    }
                }
            }
            long endTime = System.currentTimeMillis();
            //OpenSecurity.logger.error("That took " + (endTime - startTime) + " milliseconds");
		}
	}

    public static boolean getDistance(int range, BreakEvent event) {
        BlockPos eventPos = event.getPos();
        Iterable<BlockPos> blocks = BlockPos.getAllInBox(eventPos.add(-Math.abs(range * 8), -Math.abs(range * 8), -Math.abs(range * 8)), eventPos.add(range * 8, range * 8, range * 8));
        World world = event.getWorld();
        for (BlockPos pos : blocks) {
            if(world.getTileEntity(pos) instanceof TileEntitySecurityTerminal){
                return true;
            }
        }
        return false;
    }
}