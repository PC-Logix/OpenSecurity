package pcl.opensecurity.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import pcl.opensecurity.OpenSecurity;

import java.util.ArrayList;
import java.util.List;

public record AlarmStreamManifestPayload(String publicBaseUrl, int port, String token,
                                         List<Entry> entries) implements CustomPacketPayload {
    public static final Type<AlarmStreamManifestPayload> TYPE = new Type<>(OpenSecurity.id("alarm_stream_manifest"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AlarmStreamManifestPayload> STREAM_CODEC =
            StreamCodec.of((buffer, payload) -> encode(payload, buffer), AlarmStreamManifestPayload::decode);

    public AlarmStreamManifestPayload {
        entries = List.copyOf(entries);
    }

    private static void encode(AlarmStreamManifestPayload payload, FriendlyByteBuf buffer) {
        buffer.writeUtf(payload.publicBaseUrl, 2048);
        buffer.writeVarInt(payload.port);
        buffer.writeUtf(payload.token, 128);
        buffer.writeVarInt(payload.entries.size());
        for (Entry entry : payload.entries) {
            buffer.writeUtf(entry.name, 128);
            buffer.writeUtf(entry.sha256, 64);
            buffer.writeVarLong(entry.size);
        }
    }

    private static AlarmStreamManifestPayload decode(RegistryFriendlyByteBuf buffer) {
        String publicBaseUrl = buffer.readUtf(2048);
        int port = buffer.readVarInt();
        String token = buffer.readUtf(128);
        int count = buffer.readVarInt();
        if (count < 0 || count > 256) throw new IllegalArgumentException("Invalid alarm manifest size");
        List<Entry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(buffer.readUtf(128), buffer.readUtf(64), buffer.readVarLong()));
        }
        return new AlarmStreamManifestPayload(publicBaseUrl, port, token, entries);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record Entry(String name, String sha256, long size) {}
}
