package pcl.opensecurity.common;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.common.protection.Protection;
import pcl.opensecurity.networking.PacketProtectionSync;

import java.util.ArrayList;

public class OSBreakEvent {
	
	public OSBreakEvent() {
		if(OpenSecurity.debug)
			OpenSecurity.logger.info("Registering BreakEvent");
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onBlockBreak(BreakEvent event) {
		if(!OpenSecurity.registerBlockBreakEvent)
	    	return;

		if(Protection.isProtected(event.getPlayer(), Protection.UserAction.mine, event.getPos()))
			event.setCanceled(true);
	}

	@SubscribeEvent(priority=EventPriority.NORMAL)
	public void onDetonate(ExplosionEvent.Detonate event) {
		if(!OpenSecurity.registerBlockBreakEvent)
			return;

		ArrayList<BlockPos> removeBlocks = new ArrayList<>();

		for(BlockPos blockPos : event.getAffectedBlocks())
			if (Protection.isProtected(event.getWorld(), Protection.UserAction.explode, blockPos))
				removeBlocks.add(blockPos);

		event.getAffectedBlocks().removeAll(removeBlocks);
	}

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        if(event.player.world.isRemote)
            return;

        syncProtectionData(event.player.world, (EntityPlayerMP) event.player);
    }

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(event.player.world.isRemote)
            return;

        syncProtectionData(event.player.world, (EntityPlayerMP) event.player);
    }

    private void syncProtectionData(World world, EntityPlayerMP player){
        PacketProtectionSync packet = new PacketProtectionSync(world);
        OpenSecurity.network.sendTo(packet, (EntityPlayerMP) player);
    }


}