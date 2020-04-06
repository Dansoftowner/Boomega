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
@Deprecated
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

    protected void createNewConfigurationFile() {
        makeOldFileOf(getConfigurationFile());
    }

    /**
     * This method renames the given file
     * @param file
     */
    private void makeOldFileOf(File file) {

        File directoryOfFile = file.getParentFile();
        String nameOfFile = file.getName();

        File temp;
        do {
            int random = (int) (Math.random() * Math.pow(10, 5));
            temp = new File(directoryOfFile, String.format("%s_%s%d", nameOfFile, "old",  random));
        } while (temp.exists());

        if (!file.renameTo(temp)) {
            throw new UnableToCreateFileException(String.format("%s: %s %s %s", "The file called", file, "cannot be renamed to", temp));
        }
    }

    private File createAndGetFile(File file, boolean directory) {
        if (!file.exists()) {
            if (directory) {
                if (!file.mkdir()) {
                    throw new UnableToCreateFileException("Directory cannot be created: " + file.getAbsolutePath());
                }
            } else {
                try {
                    if (!file.createNewFile()) {
                        throw new UnableToCreateFileException("File cannot be created: " + file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    throw new UnableToCreateFileException(ex);
                }
            }
        }

        return file;
    }

    static final class UnableToCreateFileException extends RuntimeException {
        public UnableToCreateFileException(String message) {
            super(message);
        }

        public UnableToCreateFileException(Throwable cause) {
            super(cause);
        }
    }
}
