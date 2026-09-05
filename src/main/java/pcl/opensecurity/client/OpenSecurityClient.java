package pcl.opensecurity.client;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.AlarmClientHooks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Physical-client entry point. Keeps client-only alarm classes off dedicated servers. */
@Mod(value = OpenSecurity.MOD_ID, dist = Dist.CLIENT)
public final class OpenSecurityClient {
    public OpenSecurityClient(IEventBus modBus) {
        AlarmClientHooks.install(AlarmSoundManager::tick);
        modBus.addListener(CustomAlarmSoundPack::addPackFinder);
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> AlarmStreamClient.clear());
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> AlarmStreamClient.clear());
    }
}
