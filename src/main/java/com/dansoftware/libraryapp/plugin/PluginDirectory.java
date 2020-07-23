package com.dansoftware.libraryapp.plugin;

import com.sun.javafx.PlatformUtil;

import java.io.File;

public class PluginDirectory {

    private static final String DIRECTORY_NAME = "plugins";

    public static File getDirectory() {
        if (PlatformUtil.isWindows()) {
            return new File(String.join(
                    File.separator, System.getenv("APPDATA"), "Dansoftware", "libraryapp_2020", DIRECTORY_NAME
            ));
        } else if (PlatformUtil.isLinux()) {
            return new File(String.join(
                    File.separator, System.getProperty("user.home"), ".libraryapp2020", "profiles", DIRECTORY_NAME
            ));
        } else if (PlatformUtil.isMac()) {
            return new File(String.join(
                    File.separator, "~", "Library", "Application", "Support", "Dansoftware", "libraryapp_2020", DIRECTORY_NAME
            ));
        } else {
            return new File(DIRECTORY_NAME);
        }
    }

    private PluginDirectory() {
    }

}
