package pcl.opensecurity.client;

import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.blockentity.AlarmClientHooks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** Physical-client entry point. Keeps client-only alarm classes off dedicated servers. */
@Mod(value = OpenSecurity.MOD_ID, dist = Dist.CLIENT)
public final class OpenSecurityClient {
    public OpenSecurityClient(IEventBus modBus) {
        AlarmClientHooks.install(AlarmSoundManager::tick);
    }
}
