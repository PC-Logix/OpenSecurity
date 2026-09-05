package pcl.opensecurity.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import pcl.opensecurity.client.AlarmStreamClient;

public final class OpenSecurityNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(AlarmStreamManifestPayload.TYPE,
                AlarmStreamManifestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> AlarmStreamClient.accept(payload)));
    }

    private OpenSecurityNetwork() {}
}
