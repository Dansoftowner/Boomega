package com.dansoftware.libraryapp.config;

import com.sun.javafx.PlatformUtil;
import java.io.File;

/**
 * With this factory class can be used for creating instances of the <code>{@link ConfigurationFolder}</code> class
 * dependent on what OS that the application runs on.
 * @author Daniel Gyorffy
 */
public final class ConfigurationFolderFactory {

    private static class LinuxConfigurationFolder extends ConfigurationFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File(System.getProperty("user.home"), ".libraryapp2020/profiles/");
        }
    }

    private static class WindowsConfigurationFolder extends ConfigurationFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File(System.getenv("APPDATA"), "Dansoftware/libraryapp_2020");
        }
    }

    private static class MacConfigurationFolder extends ConfigurationFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File("~/Library/Application Support", "Dansoftware/libraryapp_2020");
        }
    }

    public ConfigurationFolder getConfigurationFolder() {
        if (PlatformUtil.isWindows())
            return new WindowsConfigurationFolder();
        else if (PlatformUtil.isLinux())
            return new LinuxConfigurationFolder();
        else if (PlatformUtil.isMac())
            return new MacConfigurationFolder();

        return null;
     }
}
