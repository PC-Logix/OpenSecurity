package pcl.opensecurity.manual;

import com.google.common.base.Charsets;
import li.cil.oc.api.manual.ContentProvider;
import pcl.opensecurity.OpenSecurity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ManualContentProvider implements ContentProvider {
    @Override
    public Iterable<String> getContent(String path) {
        if(path.contains("#"))
            path = path.substring(0, path.indexOf("#"));

        path = "assets/" + OpenSecurity.MODID + "/doc/" + (path.startsWith("/") ? path.substring(1) : path) + ".md";

        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(path);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
            final ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                //filter out lines with string screenshot in it... dirty hack to remove them from the "index/_Sidebar" page :>
                if(!line.toLowerCase().contains("screenshot"))
                    lines.add(line);
            }
            return lines;
        } catch (Throwable ignored) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
