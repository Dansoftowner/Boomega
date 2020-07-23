package com.dansoftware.libraryapp.appdata;

import com.sun.javafx.PlatformUtil;

import java.io.File;

/**
 * Factory class for creating object of {@link ApplicationDataFolder}
 *
 * @author Daniel Gyorffy
 */
@Deprecated
public final class ApplicationDataFolderFactory {

    private static ApplicationDataFolder instance;

    private static class LinuxApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootApplicationDataDirectory() {
            return new File(System.getProperty("user.home"), ".libraryapp2020/profiles/");
        }
    }

    private static class WindowsApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootApplicationDataDirectory() {
            return new File(System.getenv("APPDATA"), "Dansoftware/libraryapp_2020");
        }
    }

    private static class MacApplicationDataFolder extends ApplicationDataFolder {
        @Override
        protected File getRootApplicationDataDirectory() {
            return new File("~/Library/Application Support", "Dansoftware/libraryapp_2020");
        }
    }

    /**
     * This method creates am instance of {@link ApplicationDataFolder}
     * dependent on what OS that the application runs on.
     *
     * @return an <code>{@link ApplicationDataFolder}</code> instance
     */
    public static ApplicationDataFolder getApplicationDataFolder() {
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
