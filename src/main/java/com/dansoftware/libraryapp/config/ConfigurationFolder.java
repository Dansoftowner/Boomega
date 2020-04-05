package com.dansoftware.libraryapp.config;

import java.io.File;

/**
 * This class represents the Configuration folder of the application
 * which contains the logs, configurations, default db file and the plugins folder.
 * To access these things located in the config folder, you can get them by calling
 * the right method of this class. This class can be instantiated only by the
 * <code>{@link ConfigurationFolderFactory}</code> class.
 * @author Daniel Gyorffy
 */
public abstract class ConfigurationFolder {

    private static final String LOG_FILE = "messages.log";
    private static final String CONFIG_FILE = "settings.configuration";
    private static final String DB_FILE = "d0f0u0l0.lbadb";
    private static final String PLUGIN_FOLDER = "plugins";

    /**
     * Package-private constructor
     */
    ConfigurationFolder() {
    }

    /**
     * This method should return the root configurations folder
     * of the application.
     * @return
     */
    protected abstract File getRootConfigDirectory();

    public final File getLogFile() {
        return createAndGetFile(new File(createAndGetFile(getRootConfigDirectory(), true), LOG_FILE), false);
    }

    public final File getConfigurationFile() {
        return createAndGetFile(new File(createAndGetFile(getRootConfigDirectory(), true), CONFIG_FILE), false);
    }

    public final File getDatabaseFile() {
        return createAndGetFile(new File(createAndGetFile(getRootConfigDirectory(), true), DB_FILE), false);
    }

    public final File getPluginContainerDirectory() {
        return createAndGetFile(new File(createAndGetFile(getRootConfigDirectory(), true), PLUGIN_FOLDER), true);
    }

    private File createAndGetFile(File file, boolean directory) {
        if (!file.exists()) {
            if (directory) {
                if (!file.mkdir()) {
                    throw new CannotCreateConfigurationFileException("Directory cannot be created: " + file.getAbsolutePath());
                }
            } else {
                try {
                    if (!file.createNewFile()) {
                        throw new CannotCreateConfigurationFileException("File cannot be created: " + file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    throw new CannotCreateConfigurationFileException(ex);
                }
            }
        }

        return file;
    }

    static final class CannotCreateConfigurationFileException extends RuntimeException {
        public CannotCreateConfigurationFileException(String message) {
            super(message);
        }

        public CannotCreateConfigurationFileException(Throwable cause) {
            super(cause);
        }
    }
}
