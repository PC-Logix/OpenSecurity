package pcl.opensecurity;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import pcl.opensecurity.network.AlarmStreamManifestPayload;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/** A deliberately small, read-only HTTP server for allowlisted alarm OGGs. */
public final class AlarmOggServer {
    private static final String PATH_PREFIX = "/opensecurity/alarms/";
    private static final long MAX_TOTAL_BYTES = 256L * 1024L * 1024L;
    private static volatile Running running;

    public static synchronized void start(ServerStartedEvent event) {
        stop(null);
        if (!Config.streamCustomAlarms()) return;

        Map<String, ServedFile> files = scanFiles();
        if (files.isEmpty()) {
            Config.debug("Alarm streaming is enabled, but there are no external custom alarm OGG files to serve");
            return;
        }

        try {
            String token = UUID.randomUUID().toString().replace("-", "");
            InetSocketAddress address = new InetSocketAddress(Config.alarmStreamBindAddress(), Config.alarmStreamPort());
            HttpServer server = HttpServer.create(address, 0);
            server.createContext(PATH_PREFIX, exchange -> serve(exchange, token, files));
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            server.setExecutor(executor);
            server.start();
            running = new Running(server, executor, token, files);
            OpenSecurity.LOGGER.info("Serving {} custom alarm OGG file(s) on http://{}:{}{}",
                    files.size(), Config.alarmStreamBindAddress(), Config.alarmStreamPort(), PATH_PREFIX);
        } catch (IOException | RuntimeException exception) {
            OpenSecurity.LOGGER.error("Could not start the custom alarm OGG server", exception);
        }
    }

    public static synchronized void stop(ServerStoppingEvent ignored) {
        Running current = running;
        running = null;
        if (current != null) {
            current.server.stop(0);
            current.executor.close();
        }
    }

    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Running current = running;
        if (current == null) return;
        List<AlarmStreamManifestPayload.Entry> entries = current.files.values().stream()
                .map(file -> new AlarmStreamManifestPayload.Entry(file.name, file.sha256, file.size))
                .toList();
        PacketDistributor.sendToPlayer(player, new AlarmStreamManifestPayload(
                normalizedPublicBaseUrl(), Config.alarmStreamPort(), current.token, entries));
    }

    private static Map<String, ServedFile> scanFiles() {
        Map<String, ServedFile> files = new LinkedHashMap<>();
        long maximum = Config.alarmStreamMaxFileBytes();
        long total = 0;
        for (String name : Config.customAlarms()) {
            if (Config.isBundledAlarm(name)) continue;
            if (files.size() >= 256) {
                OpenSecurity.LOGGER.warn("Only the first 256 configured custom alarms can be streamed");
                break;
            }
            Path path = Config.customSoundDirectory().resolve(name + ".ogg");
            try {
                if (!Files.isRegularFile(path)) {
                    OpenSecurity.LOGGER.warn("Custom alarm '{}' is configured but {} does not exist", name, path);
                    continue;
                }
                long size = Files.size(path);
                if (size <= 0 || size > maximum) {
                    OpenSecurity.LOGGER.warn("Custom alarm '{}' is {} bytes; allowed range is 1..{}", name, size, maximum);
                    continue;
                }
                if (!isOgg(path)) {
                    OpenSecurity.LOGGER.warn("Custom alarm '{}' does not begin with an Ogg stream header", name);
                    continue;
                }
                if (total + size > MAX_TOTAL_BYTES) {
                    OpenSecurity.LOGGER.warn("Custom alarm streaming is limited to 256 MiB total; skipping '{}'", name);
                    continue;
                }
                files.put(name, new ServedFile(name, path, size, sha256(path)));
                total += size;
            } catch (IOException exception) {
                OpenSecurity.LOGGER.warn("Could not inspect custom alarm '{}'", name, exception);
            }
        }
        return Map.copyOf(files);
    }

    private static void serve(HttpExchange exchange, String token, Map<String, ServedFile> files) throws IOException {
        try (exchange) {
            if (!("GET".equals(exchange.getRequestMethod()) || "HEAD".equals(exchange.getRequestMethod()))) {
                exchange.getResponseHeaders().set("Allow", "GET, HEAD");
                sendEmpty(exchange, 405);
                return;
            }
            if (!hasToken(exchange, token)) {
                sendEmpty(exchange, 403);
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if (!path.startsWith(PATH_PREFIX) || !path.endsWith(".ogg")) {
                sendEmpty(exchange, 404);
                return;
            }
            String name = path.substring(PATH_PREFIX.length(), path.length() - 4);
            ServedFile file = files.get(name);
            if (file == null) {
                sendEmpty(exchange, 404);
                return;
            }

            long first = 0;
            long last = file.size - 1;
            boolean partial = false;
            String range = exchange.getRequestHeaders().getFirst("Range");
            if (range != null && range.startsWith("bytes=") && !range.substring(6).contains(",")) {
                String[] pieces = range.substring(6).split("-", -1);
                try {
                    if (pieces[0].isEmpty()) {
                        long suffixLength = Long.parseLong(pieces[1]);
                        if (suffixLength <= 0) throw new NumberFormatException();
                        first = Math.max(0, file.size - suffixLength);
                    } else {
                        first = Long.parseLong(pieces[0]);
                        if (pieces.length > 1 && !pieces[1].isEmpty()) last = Long.parseLong(pieces[1]);
                    }
                    if (first < 0 || first >= file.size || last < first) {
                        exchange.getResponseHeaders().set("Content-Range", "bytes */" + file.size);
                        sendEmpty(exchange, 416);
                        return;
                    }
                    last = Math.min(last, file.size - 1);
                    partial = true;
                } catch (NumberFormatException ignored) {
                    sendEmpty(exchange, 416);
                    return;
                }
            }

            long length = last - first + 1;
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "audio/ogg");
            headers.set("Accept-Ranges", "bytes");
            headers.set("Cache-Control", "private, max-age=3600");
            headers.set("ETag", "\"" + file.sha256 + "\"");
            if (partial) headers.set("Content-Range", "bytes " + first + "-" + last + "/" + file.size);
            if ("HEAD".equals(exchange.getRequestMethod())) {
                headers.set("Content-Length", Long.toString(length));
                exchange.sendResponseHeaders(partial ? 206 : 200, -1);
                return;
            }
            exchange.sendResponseHeaders(partial ? 206 : 200, length);
            try (var input = Files.newInputStream(file.path); var output = exchange.getResponseBody()) {
                input.skipNBytes(first);
                byte[] buffer = new byte[64 * 1024];
                long remaining = length;
                while (remaining > 0) {
                    int read = input.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                    if (read < 0) throw new IOException("Alarm file ended while serving it");
                    output.write(buffer, 0, read);
                    remaining -= read;
                }
            }
        }
    }

    private static boolean hasToken(HttpExchange exchange, String token) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null) return false;
        for (String item : query.split("&")) if (item.equals("token=" + token)) return true;
        return false;
    }

    private static void sendEmpty(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
    }

    private static String normalizedPublicBaseUrl() {
        String configured = Config.alarmStreamPublicUrl();
        while (configured.endsWith("/")) configured = configured.substring(0, configured.length() - 1);
        return configured;
    }

    private static String sha256(Path path) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (var input = Files.newInputStream(path)) {
                byte[] buffer = new byte[64 * 1024];
                for (int read; (read = input.read(buffer)) >= 0; ) if (read > 0) digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    private static boolean isOgg(Path path) throws IOException {
        try (var input = Files.newInputStream(path)) {
            return input.read() == 'O' && input.read() == 'g' && input.read() == 'g' && input.read() == 'S';
        }
    }

    private record ServedFile(String name, Path path, long size, String sha256) {}
    private record Running(HttpServer server, ExecutorService executor, String token, Map<String, ServedFile> files) {}

    private AlarmOggServer() {}
}
