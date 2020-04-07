package com.dansoftware.libraryapp.appdata;

import com.sun.javafx.PlatformUtil;

import java.io.File;

/**
 * With this factory class can be used for creating instances of the <code>{@link ApplicationDataFolder}</code> class
 * dependent on what OS that the application runs on.
 *
 * @author Daniel Gyorffy
 */
public final class ApplicationDataFolderFactory {

    private static ApplicationDataFolder instance;

    private static class LinuxApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File(System.getProperty("user.home"), ".libraryapp2020/profiles/");
        }
    }

    private static class WindowsApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File(System.getenv("APPDATA"), "Dansoftware/libraryapp_2020");
        }
    }

    private static class MacApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootConfigDirectory() {
            return new File("~/Library/Application Support", "Dansoftware/libraryapp_2020");
        }
    }

    public static ApplicationDataFolder getConfigurationFolder() {
        if (instance == null) {
            if (PlatformUtil.isWindows())
                instance = new WindowsApplicationDataFolder();
            else if (PlatformUtil.isLinux())
                instance = new LinuxApplicationDataFolder();
            else if (PlatformUtil.isMac())
                instance = new MacApplicationDataFolder();
        }

        return instance;
    }
}
