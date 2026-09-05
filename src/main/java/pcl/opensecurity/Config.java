package pcl.opensecurity;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** OpenSecurity's NeoForge configuration, preserving the 1.12 option names and defaults. */
public final class Config {
    public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec CLIENT_SPEC;

    private static final ModConfigSpec.BooleanValue ENABLE_PLAY_SOUND_AT;
    private static final ModConfigSpec.BooleanValue ENABLE_DEBUG_MESSAGES;
    private static final ModConfigSpec.BooleanValue IGNORE_UUIDS;
    private static final ModConfigSpec.BooleanValue REGISTER_BLOCK_BREAK;
    private static final ModConfigSpec.BooleanValue TURRET_REVERSE_ROTATION;
    private static final ModConfigSpec.BooleanValue BIGGER_EEPROM;
    private static final ModConfigSpec.IntValue RFID_MAX_RANGE;
    private static final ModConfigSpec.IntValue ENTITY_DETECTOR_MAX_RANGE;
    private static final ModConfigSpec.IntValue ALARM_MAX_RANGE;
    private static final ModConfigSpec.BooleanValue INSTANT_NANO_FOG;
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CUSTOM_ALARMS;
    private static final ModConfigSpec.BooleanValue STREAM_CUSTOM_ALARMS;
    private static final ModConfigSpec.ConfigValue<String> ALARM_STREAM_BIND_ADDRESS;
    private static final ModConfigSpec.IntValue ALARM_STREAM_PORT;
    private static final ModConfigSpec.ConfigValue<String> ALARM_STREAM_PUBLIC_URL;
    private static final ModConfigSpec.IntValue ALARM_STREAM_MAX_FILE_MIB;

    private static final ModConfigSpec.IntValue NANO_FOG_SWARM_RESOLUTION;

    static {
        ModConfigSpec.Builder server = new ModConfigSpec.Builder();
        server.push("general");
        ENABLE_PLAY_SOUND_AT = server
                .comment("Enable the alarm playSoundAt callback. This permits computers to play any sound event at any location in range and may be abused.")
                .define("enableplaySoundAt", false);
        ENABLE_DEBUG_MESSAGES = server
                .comment("Enable OpenSecurity debug messages in the log.")
                .define("enableDebugMessages", false);
        IGNORE_UUIDS = server
                .comment("Return '-1' instead of card UUIDs from RFID and magnetic-card components.")
                .define("ignoreUUIDs", false);
        REGISTER_BLOCK_BREAK = server
                .comment("Protect Door Controllers, Security Doors, and Security Terminal areas from block breaking.")
                .define("registerBlockBreak", true);
        TURRET_REVERSE_ROTATION = server
                .comment("Reverse rendered turret yaw for compatibility with the legacy rotation direction.")
                .define("turretReverseRotation", true);
        BIGGER_EEPROM = server
                .comment("Allow the Card Writer to flash EEPROM code and data up to twice OpenComputers' configured limits.")
                .define("biggerEEPROM", false);
        RFID_MAX_RANGE = server
                .comment("Maximum RFID Reader range in blocks.")
                .defineInRange("rfidMaxRange", 16, 1, 64);
        ENTITY_DETECTOR_MAX_RANGE = server
                .comment("Maximum Entity Detector range in blocks.")
                .defineInRange("entityDetectorMaxRange", 16, 1, 64);
        ALARM_MAX_RANGE = server
                .comment("Maximum Alarm and playSoundAt range in blocks.")
                .defineInRange("alarmMaxRange", 15, 1, 96);
        INSTANT_NANO_FOG = server
                .comment("Build NanoFog blocks immediately instead of using the legacy swarm animation.")
                .define("instantNanoFog", false);
        CUSTOM_ALARMS = server
                .comment("Alarm sound names exposed by listSounds. Put matching <name>.ogg files in mods/OpenSecurity/assets/opensecurity/sounds/alarms on the server. Clients fetch them automatically when streaming is enabled.")
                .defineListAllowEmpty("customAlarms", List.of("klaxon1", "klaxon2"), () -> "custom_alarm", Config::validCustomAlarmEntry);
        STREAM_CUSTOM_ALARMS = server
                .comment("Serve allowlisted custom alarm OGG files to modded clients over a small embedded HTTP server.")
                .define("streamCustomAlarms", true);
        ALARM_STREAM_BIND_ADDRESS = server
                .comment("Local address used by the embedded alarm HTTP server. 0.0.0.0 listens on every interface.")
                .define("alarmStreamBindAddress", "0.0.0.0");
        ALARM_STREAM_PORT = server
                .comment("TCP port used by the embedded alarm HTTP server.")
                .defineInRange("alarmStreamPort", 8765, 1, 65535);
        ALARM_STREAM_PUBLIC_URL = server
                .comment("Optional externally reachable base URL, for example https://minecraft.example.com/opensecurity. Leave empty to use the Minecraft server host and alarmStreamPort.")
                .define("alarmStreamPublicUrl", "");
        ALARM_STREAM_MAX_FILE_MIB = server
                .comment("Maximum size of one custom alarm served to clients, in MiB.")
                .defineInRange("alarmStreamMaxFileMiB", 16, 1, 128);
        server.pop();
        COMMON_SPEC = server.build();

        ModConfigSpec.Builder client = new ModConfigSpec.Builder();
        client.push("client");
        NANO_FOG_SWARM_RESOLUTION = client
                .comment("Resolution used to render NanoFog swarms.")
                .defineInRange("nanoFogSwarmResolution", 8, 2, 16);
        client.pop();
        CLIENT_SPEC = client.build();
    }

    public static boolean enablePlaySoundAt() { return ENABLE_PLAY_SOUND_AT.get(); }
    public static boolean enableDebugMessages() {
        try {
            return ENABLE_DEBUG_MESSAGES.get();
        } catch (IllegalStateException ignored) {
            return ENABLE_DEBUG_MESSAGES.getDefault();
        }
    }
    public static boolean ignoreUUIDs() { return IGNORE_UUIDS.get(); }
    public static boolean registerBlockBreak() { return REGISTER_BLOCK_BREAK.get(); }
    public static boolean turretReverseRotation() { return TURRET_REVERSE_ROTATION.get(); }
    public static boolean biggerEEPROM() { return BIGGER_EEPROM.get(); }
    public static int rfidMaxRange() { return RFID_MAX_RANGE.get(); }
    public static int entityDetectorMaxRange() { return ENTITY_DETECTOR_MAX_RANGE.get(); }
    public static int alarmMaxRange() { return ALARM_MAX_RANGE.get(); }
    public static boolean instantNanoFog() { return INSTANT_NANO_FOG.get(); }
    public static boolean streamCustomAlarms() { return STREAM_CUSTOM_ALARMS.get(); }
    public static String alarmStreamBindAddress() { return ALARM_STREAM_BIND_ADDRESS.get().trim(); }
    public static int alarmStreamPort() { return ALARM_STREAM_PORT.get(); }
    public static String alarmStreamPublicUrl() { return ALARM_STREAM_PUBLIC_URL.get().trim(); }
    public static long alarmStreamMaxFileBytes() { return ALARM_STREAM_MAX_FILE_MIB.get() * 1024L * 1024L; }
    public static int nanoFogSwarmResolution() { return NANO_FOG_SWARM_RESOLUTION.get(); }

    public static List<String> customAlarms() {
        List<? extends String> configured;
        try {
            configured = CUSTOM_ALARMS.get();
        } catch (IllegalStateException ignored) {
            configured = CUSTOM_ALARMS.getDefault();
        }
        LinkedHashSet<String> names = new LinkedHashSet<>();
        for (String entry : configured) {
            String normalized = normalizeAlarmName(entry);
            if (normalized != null) names.add(normalized);
        }
        return List.copyOf(names);
    }

    public static List<String> availableAlarmSounds() {
        Set<String> sounds = new LinkedHashSet<>();
        Path directory = customSoundDirectory();
        for (String name : customAlarms()) {
            if (isBundledAlarm(name) || Files.isRegularFile(directory.resolve(name + ".ogg"))) sounds.add(name);
        }
        return List.copyOf(sounds);
    }

    public static boolean isAllowedAlarmSound(String requested) {
        if (requested == null) return false;
        String name = requested.startsWith(OpenSecurity.MOD_ID + ":")
                ? requested.substring(OpenSecurity.MOD_ID.length() + 1) : requested;
        return availableAlarmSounds().contains(name);
    }

    public static Path customSoundDirectory() {
        Path gameDirectory = net.neoforged.fml.loading.FMLPaths.GAMEDIR.get();
        Path current = gameDirectory.resolve("mods/OpenSecurity/assets/opensecurity/sounds/alarms");
        Path legacy = gameDirectory.resolve("mods/OpenSecurity/sounds/alarms");
        return Files.isDirectory(current) || !Files.isDirectory(legacy) ? current : legacy;
    }

    public static String exposedUuid(String uuid) {
        return ignoreUUIDs() ? "-1" : uuid;
    }

    private static boolean validCustomAlarmEntry(Object value) {
        return value instanceof String string && normalizeAlarmName(string) != null;
    }

    public static boolean isBundledAlarm(String name) {
        return "klaxon1".equals(name) || "klaxon2".equals(name);
    }

    public static void debug(String message, Object... arguments) {
        if (enableDebugMessages()) OpenSecurity.LOGGER.info("[OpenSecurity debug] " + message, arguments);
    }

    private static String normalizeAlarmName(String value) {
        String name = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (name.endsWith(".ogg")) name = name.substring(0, name.length() - 4);
        if (name.isEmpty() || !name.matches("[a-z0-9_.-]+")) return null;
        return name;
    }

    private Config() {}
}
