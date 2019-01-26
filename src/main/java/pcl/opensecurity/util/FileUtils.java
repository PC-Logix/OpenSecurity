package pcl.opensecurity.util;

import java.io.File;
import java.util.HashSet;

public class FileUtils {
    public static HashSet<File> listFilesForPath(String path) {
        return listFilesForPath(path, true);
    }

    public static HashSet<File> listFilesForPath(String path, boolean recursive){
        HashSet<File> files = new HashSet<>();

        File folder = new File(path);

        if (folder.listFiles() == null)
            return files;

        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory())
                files.add(fileEntry);
            else if(recursive)
                files.addAll(listFilesForPath(fileEntry.getPath()));

        }

        return files;
    }
}
