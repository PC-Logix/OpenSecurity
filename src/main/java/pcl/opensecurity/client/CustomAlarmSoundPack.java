package pcl.opensecurity.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import pcl.opensecurity.Config;
import pcl.opensecurity.OpenSecurity;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

/** Always-active client resource pack backed by the legacy external alarm sound directory. */
public final class CustomAlarmSoundPack extends AbstractPackResources {
    private static final String PACK_ID = "opensecurity/custom_alarm_sounds";
    private static final ResourceLocation SOUNDS_JSON = OpenSecurity.id("sounds.json");

    private CustomAlarmSoundPack(PackLocationInfo location) {
        super(location);
    }

    public static void addPackFinder(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        Config.debug("Loading custom alarm sounds {} from {}", Config.customAlarms(), Config.customSoundDirectory());

        PackLocationInfo location = new PackLocationInfo(PACK_ID,
                Component.literal("OpenSecurity Custom Alarm Sounds"), PackSource.BUILT_IN, Optional.empty());
        Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier() {
            @Override public PackResources openPrimary(PackLocationInfo info) { return new CustomAlarmSoundPack(info); }
            @Override public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) { return openPrimary(info); }
        };
        Pack pack = Pack.readMetaAndCreate(location, resources, PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.TOP, true));
        if (pack != null) event.addRepositorySource(consumer -> consumer.accept(pack));
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length != 1 || !"pack.mcmeta".equals(elements[0])) return null;
        int format = SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
        return bytes("{\"pack\":{\"pack_format\":" + format
                + ",\"description\":\"OpenSecurity custom alarm sounds\"}}");
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES || !OpenSecurity.MOD_ID.equals(location.getNamespace())) return null;
        if (SOUNDS_JSON.equals(location)) return bytes(createSoundsJson());

        String path = location.getPath();
        String prefix = "sounds/alarms/";
        if (!path.startsWith(prefix) || !path.endsWith(".ogg")) return null;
        String name = path.substring(prefix.length(), path.length() - 4);
        if (!Config.customAlarms().contains(name)) return null;
        Path file = Config.customSoundDirectory().resolve(name + ".ogg");
        return Files.isRegularFile(file) ? IoSupplier.create(file) : null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES || !OpenSecurity.MOD_ID.equals(namespace)) return;
        if (path.isEmpty() || "sounds.json".startsWith(path)) output.accept(SOUNDS_JSON, bytes(createSoundsJson()));
        for (String name : Config.customAlarms()) {
            if (Config.isBundledAlarm(name)) continue;
            ResourceLocation id = OpenSecurity.id("sounds/alarms/" + name + ".ogg");
            Path file = Config.customSoundDirectory().resolve(name + ".ogg");
            if (id.getPath().startsWith(path) && Files.isRegularFile(file)) output.accept(id, IoSupplier.create(file));
        }
    }

    @Override public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? Set.of(OpenSecurity.MOD_ID) : Set.of();
    }

    @Override public void close() {}

    private static String createSoundsJson() {
        JsonObject root = new JsonObject();
        Path directory = Config.customSoundDirectory();
        for (String name : Config.customAlarms()) {
            if (Config.isBundledAlarm(name)) continue;
            if (!Files.isRegularFile(directory.resolve(name + ".ogg"))) continue;
            JsonObject event = new JsonObject();
            event.addProperty("category", "block");
            JsonObject sound = new JsonObject();
            sound.addProperty("name", OpenSecurity.MOD_ID + ":alarms/" + name);
            sound.addProperty("stream", false);
            JsonArray sounds = new JsonArray();
            sounds.add(sound);
            event.add("sounds", sounds);
            root.add(name, event);
        }
        return root.toString();
    }

    private static IoSupplier<InputStream> bytes(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }
}
