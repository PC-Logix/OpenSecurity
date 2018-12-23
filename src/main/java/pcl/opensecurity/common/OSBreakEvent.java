package pcl.opensecurity.common;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.protection.Protection;

import java.util.ArrayList;

public class OSBreakEvent {
	
	public OSBreakEvent() {
		OpenSecurity.logger.info("Registering BreakEvent");
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onBlockBreak(BreakEvent event) {
		if(event.getWorld().isRemote || !OpenSecurity.registerBlockBreakEvent)
	    	return;

		if(Protection.isProtected(event.getPlayer(), Protection.UserAction.mine, event.getPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onDetonate(ExplosionEvent.Detonate event) {
		if(event.getWorld().isRemote || !OpenSecurity.registerBlockBreakEvent)
			return;

		ArrayList<BlockPos> removeBlocks = new ArrayList<>();

		for(BlockPos blockPos : event.getAffectedBlocks())
			if (Protection.isProtected(event.getWorld(), Protection.UserAction.explode, blockPos))
				removeBlocks.add(blockPos);

		event.getAffectedBlocks().removeAll(removeBlocks);
	}

}