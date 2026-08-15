package pcl.opensecurity.client;

import li.cil.oc.api.manual.ContentProvider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Loads the raw GitHub Wiki format used by OpenSecurity's documentation.
 *
 * <p>The old integration intentionally accepted extensionless links and
 * loaded pages from the classpath. Keeping that behavior means the wiki can
 * continue to be mirrored without changing its page names or links.</p>
 */
final class WikiContentProvider implements ContentProvider {
    private static final String PATH_PREFIX = "opensecurity/";
    private static final String RESOURCE_PREFIX = "assets/opensecurity/doc/";
    private static final String EXTERNAL_IMAGE = "!\\[[^]]*]\\((?:http|https)://[^)]*\\)";

    @Override
    public Iterable<String> getContent(String path) {
        if (path == null || !path.startsWith(PATH_PREFIX)) return null;

        String wikiPath = path.substring(PATH_PREFIX.length());
        int anchor = wikiPath.indexOf('#');
        if (anchor >= 0) wikiPath = wikiPath.substring(0, anchor);
        if (!wikiPath.endsWith(".md")) wikiPath += ".md";

        InputStream stream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PREFIX + wikiPath);
        if (stream == null) return null;

        ArrayList<String> lines = new ArrayList<>();
        boolean inCodeBlock = false;
        try (stream; BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase(Locale.ROOT).contains("screenshot")) continue;

                line = line.replaceAll(EXTERNAL_IMAGE, "");
                if (line.contains("```")) {
                    line = line.replace("```lua", "").replace("```", "");
                    inCodeBlock = !inCodeBlock;
                }
                if (inCodeBlock) line = "`" + line + "`";
                lines.add(line);
            }
            return lines;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
