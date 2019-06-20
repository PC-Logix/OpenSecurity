package pcl.opensecurity.manual;

import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class ManualContentProvider {
    private static final String regExImageTag = "!\\[[^]]*]\\((http|https)://[^)]*\\)"; // regEx for ![description](http(s)://imageURL)

    public Iterable<String> getContent(String path) {
        final ArrayList<String> lines = new ArrayList<>();

        if(path.contains("#")) //remove jumpmarks from uri
            path = path.substring(0, path.indexOf("#"));

        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(path + ".md");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
            String line;
            boolean inCodeBlock = false;

            while ((line = reader.readLine()) != null) {
                //filter out lines with string screenshot in it... dirty hack to remove them from the "index/_Sidebar" page :>
                if(line.toLowerCase().contains("screenshot"))
                    continue;

                line = line.replaceAll(regExImageTag, ""); // strip of markdown image tags to avoid logspamming of not resolvable image uris

                if (line.contains("```")) { // if this line would start/stop a codeblock we change our inCodeBlock var so that code tags are added to each line
                    line = line.replaceAll("```lua", "").replaceAll("```", ""); // strip of codeblock tags, as they aren't supported
                    inCodeBlock = !inCodeBlock;
                }

                if (inCodeBlock)
                    line = "`" + line + "`";

                lines.add(line);
            }
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (is != null)
                try { is.close(); } catch (IOException ignored) {}
        }

        return lines;
    }
}
