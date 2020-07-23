package com.dansoftware.libraryapp.plugin;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

public class PluginClassLoader extends URLClassLoader {

    private static final PluginClassLoader instance = new PluginClassLoader();

    private PluginClassLoader() {
        super(getSources(), ClassLoader.getSystemClassLoader());
    }

    private static URL[] getSources() {
        Pattern pattern = Pattern.compile(".*\\.jar");

        try {
            return FileUtils.toURLs(
                    PluginDirectory.getDirectory().listFiles((dir, name) -> pattern.matcher(name).matches())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PluginClassLoader getInstance() {
        return instance;
    }
}
