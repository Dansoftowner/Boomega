package com.dansoftware.libraryapp.appdata;

import com.sun.javafx.PlatformUtil;

import java.io.File;

public class ConfigFile {

    public static final String FILE_NAME = "config.conf";

    public static File getFile() {
        if (PlatformUtil.isWindows()) {
            return new File(String.join(
                    File.separator, System.getProperty(/*"APPDATA"*/ "user.home"), "Dansoftware", "libraryapp_2020", FILE_NAME
            ));
        } else if (PlatformUtil.isLinux()) {
            return new File(String.join(
                    File.separator, System.getProperty("user.home"), ".libraryapp2020", "profiles", "config", FILE_NAME
            ));
        } else if (PlatformUtil.isMac()) {
            return new File(String.join(
                    File.separator, "~", "Library", "Application", "Support", "Dansoftware", "libraryapp_2020", FILE_NAME
            ));
        } else {
            return new File(FILE_NAME);
        }
    }

    private ConfigFile() {
    }
}
