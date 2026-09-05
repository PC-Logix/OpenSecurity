package pcl.opensecurity.client;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLPaths;
import pcl.opensecurity.OpenSecurity;
import pcl.opensecurity.network.AlarmStreamManifestPayload;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/** Downloads and verifies a server's alarm manifest before exposing it to the resource pack. */
public final class AlarmStreamClient {
    private static final long ABSOLUTE_MAX_FILE_SIZE = 128L * 1024L * 1024L;
    private static final long ABSOLUTE_MAX_TOTAL_SIZE = 256L * 1024L * 1024L;
    private static final AtomicLong GENERATION = new AtomicLong();
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public static void accept(AlarmStreamManifestPayload payload) {
        long generation = GENERATION.incrementAndGet();
        Minecraft minecraft = Minecraft.getInstance();
        String baseUrl;
        try {
            baseUrl = resolveBaseUrl(minecraft, payload);
            validateManifest(payload);
        } catch (RuntimeException exception) {
            OpenSecurity.LOGGER.warn("Rejected custom alarm stream manifest", exception);
            return;
        }

        CompletableFuture.runAsync(() -> download(generation, baseUrl, payload));
    }

    public static void clear() {
        GENERATION.incrementAndGet();
        CustomAlarmSoundPack.useStreamedSounds(null, List.of());
        Minecraft.getInstance().reloadResourcePacks().exceptionally(exception -> {
            OpenSecurity.LOGGER.error("Could not unload streamed custom alarm sounds", exception);
            return null;
        });
    }

    private static void download(long generation, String baseUrl, AlarmStreamManifestPayload payload) {
        try {
            Path cache = cacheDirectory(baseUrl);
            Files.createDirectories(cache);
            List<String> names = new ArrayList<>();
            for (AlarmStreamManifestPayload.Entry entry : payload.entries()) {
                if (generation != GENERATION.get()) return;
                Path destination = cache.resolve(entry.name() + ".ogg");
                if (!matches(destination, entry)) fetch(baseUrl, payload.token(), entry, destination);
                names.add(entry.name());
            }
            if (generation != GENERATION.get()) return;
            Minecraft.getInstance().execute(() -> {
                if (generation != GENERATION.get()) return;
                CustomAlarmSoundPack.useStreamedSounds(cache, names);
                Minecraft.getInstance().reloadResourcePacks().exceptionally(exception -> {
                    OpenSecurity.LOGGER.error("Could not reload streamed custom alarm sounds", exception);
                    return null;
                });
                OpenSecurity.LOGGER.info("Loaded {} custom alarm sound(s) from {}", names.size(), baseUrl);
            });
        } catch (Exception exception) {
            OpenSecurity.LOGGER.error("Could not download custom alarms from {}", baseUrl, exception);
        }
    }

    private static void fetch(String baseUrl, String token, AlarmStreamManifestPayload.Entry entry,
                              Path destination) throws IOException, InterruptedException {
        URI uri = URI.create(baseUrl + "/alarms/" + entry.name() + ".ogg?token=" + token);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(60))
                .header("Accept", "audio/ogg")
                .GET().build();
        HttpResponse<InputStream> response = HTTP.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            response.body().close();
            throw new IOException("HTTP " + response.statusCode() + " for " + entry.name());
        }
        long declared = response.headers().firstValueAsLong("Content-Length").orElse(-1);
        if (declared != entry.size()) {
            response.body().close();
            throw new IOException("Wrong Content-Length for " + entry.name());
        }

        Path temporary = Files.createTempFile(destination.getParent(), entry.name() + "-", ".part");
        try {
            MessageDigest digest = digest();
            long written = 0;
            try (InputStream input = response.body(); var output = Files.newOutputStream(temporary)) {
                byte[] buffer = new byte[64 * 1024];
                for (int read; (read = input.read(buffer)) >= 0; ) {
                    if (read == 0) continue;
                    written += read;
                    if (written > entry.size()) throw new IOException("Oversized response for " + entry.name());
                    digest.update(buffer, 0, read);
                    output.write(buffer, 0, read);
                }
            }
            String actualHash = HexFormat.of().formatHex(digest.digest());
            if (written != entry.size() || !actualHash.equals(entry.sha256())) {
                throw new IOException("Hash or size mismatch for " + entry.name());
            }
            if (!isOgg(temporary)) throw new IOException("Invalid OGG header for " + entry.name());
            try {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static boolean matches(Path file, AlarmStreamManifestPayload.Entry entry) throws IOException {
        return Files.isRegularFile(file) && Files.size(file) == entry.size()
                && isOgg(file) && sha256(file).equals(entry.sha256());
    }

    private static void validateManifest(AlarmStreamManifestPayload payload) {
        if (payload.port() < 1 || payload.port() > 65535 || !payload.token().matches("[a-f0-9]{32}")) {
            throw new IllegalArgumentException("Invalid endpoint");
        }
        if (payload.entries().size() > 256) throw new IllegalArgumentException("Too many alarm files");
        long total = 0;
        for (AlarmStreamManifestPayload.Entry entry : payload.entries()) {
            if (!entry.name().matches("[a-z0-9_.-]+") || !entry.sha256().matches("[a-f0-9]{64}")
                    || entry.size() <= 0 || entry.size() > ABSOLUTE_MAX_FILE_SIZE) {
                throw new IllegalArgumentException("Invalid alarm manifest entry");
            }
            total += entry.size();
            if (total > ABSOLUTE_MAX_TOTAL_SIZE) throw new IllegalArgumentException("Alarm manifest is too large");
        }
    }

    private static String resolveBaseUrl(Minecraft minecraft, AlarmStreamManifestPayload payload) {
        String configured = payload.publicBaseUrl();
        if (!configured.isBlank()) {
            URI uri = URI.create(configured);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null) throw new IllegalArgumentException("Invalid public alarm URL");
            while (configured.endsWith("/")) configured = configured.substring(0, configured.length() - 1);
            return configured;
        }

        String host = "127.0.0.1";
        if (minecraft.getConnection() != null) {
            SocketAddress remote = minecraft.getConnection().getConnection().getRemoteAddress();
            if (remote instanceof InetSocketAddress inet) host = inet.getHostString();
        }
        if (host.contains(":") && !host.startsWith("[")) host = "[" + host + "]";
        return "http://" + host + ":" + payload.port() + "/opensecurity";
    }

    private static Path cacheDirectory(String baseUrl) {
        String key = HexFormat.of().formatHex(digest().digest(baseUrl.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .substring(0, 16);
        return FMLPaths.GAMEDIR.get().resolve("config/opensecurity/alarm-cache").resolve(key);
    }

    private static String sha256(Path path) throws IOException {
        MessageDigest digest = digest();
        try (InputStream input = Files.newInputStream(path)) {
            byte[] buffer = new byte[64 * 1024];
            for (int read; (read = input.read(buffer)) >= 0; ) if (read > 0) digest.update(buffer, 0, read);
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static boolean isOgg(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            return input.read() == 'O' && input.read() == 'g' && input.read() == 'g' && input.read() == 'S';
        }
    }

    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException(impossible);
        }
    }

    private AlarmStreamClient() {}
}
