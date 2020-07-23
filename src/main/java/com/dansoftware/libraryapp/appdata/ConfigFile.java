package com.dansoftware.libraryapp.appdata;

import com.dansoftware.libraryapp.util.FileUtils;
import com.sun.javafx.PlatformUtil;

import java.io.File;
import java.io.IOException;

public class ConfigFile extends File {

    public static final String FILE_NAME = "config.conf";

    private static class WindowsPath {
        @Override
        public String toString() {
            return String.join(File.separator, System.getProperty(/*"APPDATA"*/ "user.home"), "Dansoftware", "libraryapp_2020", FILE_NAME);
        }
    }

    private static class LinuxPath {
        @Override
        public String toString() {
            return String.join(File.separator, System.getProperty("user.home"), ".libraryapp2020", "profiles", "config", FILE_NAME);
        }
    }

    private static class MacPath {
        @Override
        public String toString() {
            return String.join(File.separator,"~", "Library", "Application", "Support", "Dansoftware", "libraryapp_2020", FILE_NAME);
        }
    }

    private static class PathFactory {
        private static String construct() {
            if (PlatformUtil.isWindows()) {
                return new WindowsPath().toString();
            } else if (PlatformUtil.isLinux()) {
                return new LinuxPath().toString();
            } else if (PlatformUtil.isMac()) {
                return new MacPath().toString();
            } else {
                return FILE_NAME;
            }
        }
    }

    public ConfigFile() {
        super(PathFactory.construct());
    }
}
