package com.dansoftware.libraryapp.plugin;

import com.dansoftware.libraryapp.util.OsInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Represents the directory that holds the plugin jar files on the disk.
 *
 * @author Daniel Gyorffy
 */
public class PluginDirectory extends File {

    private static final String DIRECTORY_NAME = "plugin";

    private static final String PATH;

    static {
        if (OsInfo.isWindows()) {
            PATH = String.join(File.separator, System.getenv("APPDATA"), "Dansoftware", "libraryapp2020", DIRECTORY_NAME);
        } else if (OsInfo.isLinux()) {
            PATH = String.join(File.separator, System.getProperty("user.home"), ".libraryapp2020", "profiles", DIRECTORY_NAME);
        } else if (OsInfo.isMac()) {
            PATH = String.join(File.separator, "~", "Library", "Application", "Support", "Dansoftware", "libraryapp2020", DIRECTORY_NAME);
        } else {
            PATH = DIRECTORY_NAME;
        }
    }

    public PluginDirectory() {
        super(PATH);
        if (!exists()) {
            mkdirs();
        }
    }

    /**
     * Returns all jar files located in the directory
     *
     * @return the array of {@link File} objects
     */
    public File[] getPluginFiles() {
        Pattern pattern = Pattern.compile(".*\\.jar");
        return this.listFiles((dir, name) -> pattern.matcher(name).matches());
    }

    /**
     * Returns all jar files located in the directory as {@link URL} objects
     *
     * @return the array of {@link URL}s
     */
    public URL[] getPluginFileURLS() {
        try {
            return FileUtils.toURLs(getPluginFiles());
        } catch (IOException e) {
            return null;
        }
    }
}
