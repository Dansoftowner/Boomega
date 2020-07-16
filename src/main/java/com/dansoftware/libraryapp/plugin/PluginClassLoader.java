package com.dansoftware.libraryapp.plugin;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

public class PluginClassLoader extends URLClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClassLoader.class);
    private static final PluginClassLoader instance = new PluginClassLoader();

    private PluginClassLoader() {
        super(getSources(), ClassLoader.getSystemClassLoader());
    }

    private static URL[] getSources() {
        Pattern pattern = Pattern.compile(".*\\.jar");
        ApplicationDataFolder appdataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        try {
            return FileUtils.toURLs(appdataFolder.getPluginContainerDirectory()
                    .listFiles((dir, name) -> pattern.matcher(name).matches()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return null;
    }

    public static PluginClassLoader getInstance() {
        return instance;
    }
}
