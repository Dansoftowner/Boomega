package com.dansoftware.libraryapp.plugin;

import java.net.URLClassLoader;

/**
 * The {@link PluginClassLoader} can load classes from both the class-path and the third-party plugins located
 * in the plugin-directory.
 *
 * @author Daniel Gyorffy
 */
public class PluginClassLoader extends URLClassLoader {

    private static final PluginClassLoader instance = new PluginClassLoader();

    private PluginClassLoader() {
        super(new PluginDirectory().getPluginFileURLS(), ClassLoader.getSystemClassLoader());
    }

    public static PluginClassLoader getInstance() {
        return instance;
    }
}
