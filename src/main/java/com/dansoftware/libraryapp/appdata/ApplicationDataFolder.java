package com.dansoftware.libraryapp.appdata;

import java.io.File;
import java.util.logging.Logger;

/**
 * This class represents the Configuration folder of the application
 * which contains the logs, configurations, default db file and the plugins folder.
 * To access these things located in the config folder, you can get them by calling
 * the right method of this class. This class can be instantiated only by the
 * <code>{@link ApplicationDataFolderFactory}</code> class.
 *
 * @author Daniel Gyorffy
 */
public abstract class ApplicationDataFolder {

    private static final Logger logger = Logger.getLogger(ApplicationDataFolder.class.getName());

    /**
     * This enum represents the folders/files in the application data folder of this program
     */
    private enum ApplicationDataFolderElement {

        /**
         * Represents the configuration file of the program
         */
        CONFIG_FILE("settings.configuration", Boolean.FALSE),

        /**
         * Represents the default database file for the program
         */
        DB_FILE("d0f0u0l0.lbadb", Boolean.FALSE),

        /**
         * Represents the folder of plugins
         */
        PLUGIN_FOLDER("plugins", Boolean.TRUE);

        /**
         * Defines the concrete file name
         */
        private final String fileName;

        /**
         * Represents that this element is a directory or a file
         */
        private final boolean directory;


        ApplicationDataFolderElement(String name, boolean directory) {
            this.fileName = name;
            this.directory = directory;
        }
    }

    /**
     * Package-private constructor
     */
    ApplicationDataFolder() {
    }

    /**
     * This method should return the root configurations folder
     * of the application.
     *
     * @return the object representation of the directory
     */
    protected abstract File getRootApplicationDataDirectory();

    /**
     * @return the object representation of the directory that contains logs
     */
    public final File getConfigurationFile() {
        return getFileOf(ApplicationDataFolderElement.CONFIG_FILE);
    }

    /**
     * @return the object representation of the default database file
     */
    public final File getDefaultDatabaseFile() {
        return getFileOf(ApplicationDataFolderElement.DB_FILE);
    }

    /**
     * @return the object representation of the directory that contains the plugins
     */
    public final File getPluginContainerDirectory() {
        return getFileOf(ApplicationDataFolderElement.PLUGIN_FOLDER);
    }

    /**
     * This method returns the File object version of the
     * <code>{@link ApplicationDataFolderElement}</code> type,
     * and also creates the file on the disk.
     *
     * @param element the <code>{@link ApplicationDataFolderElement}</code> constant
     * @return <code>{@link File}</code> object that represents the given directory/file
     * from the Appdata folder
     */
    private File getFileOf(ApplicationDataFolderElement element) {
        File rootDirectory = createFile(getRootApplicationDataDirectory(), true);
        return createFile(new File(rootDirectory, element.fileName), element.directory);
    }

    /**
     * Creates a new Configuration file for the program
     */
    void createNewConfigurationFile() throws UnableToCreateFileException {
        makeOldFileOf(getConfigurationFile());

        logger.info("New configuration file created!");
    }

    /**
     * This method renames the given file this way:
     * <ul>
     *     <li>Generates a random 5-digit number</li>
     *     <li>Adds the '_old' word to the original name of the file</li>
     *     <li>Adds the random number to the end of the file name</li>
     * </ul>
     * If a file already exists with the generated file name, the process
     * will be repeated again
     *
     * This is good practise because we don't delete completely the old file,
     * we just rename it and then the program can use a new file with the
     * original name
     *
     * @param file the file that we want to rename
     */
    private void makeOldFileOf(File file) {

        File directoryOfFile = file.getParentFile();
        String nameOfFile = file.getName();

        File generated;
        do {
            int random = (int) (Math.random() * Math.pow(10, 5));
            generated = new File(directoryOfFile, String.format("%s_%s%d", nameOfFile, "old",  random));
        } while (generated.exists());

        if (!file.renameTo(generated)) {
            throw new UnableToCreateFileException("The file : '" + file.getAbsolutePath() + "' cannot be renamed to: '" + generated + "'");
        }
    }

    /**
     * This method creates the file and then returns the file.
     *
     * @param file the object that represents the file that we want to create
     * @param directory a boolean value that represents what kind of 'file' that we want to create.
     *                  True if we want to create a directory, false if we want to create a regular file.
     * @return the file that created <i>basically the same file object that passed as parameter</i>
     */
    private File createFile(File file, boolean directory) {
        if (!file.exists()) {
            if (directory) {
                if (!file.mkdirs()) {
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


    /**
     * Exception class for cases when this class can't create/modify files
     */
    static final class UnableToCreateFileException extends RuntimeException {
        public UnableToCreateFileException(String message) {
            super(message);
        }

        public UnableToCreateFileException(Throwable cause) {
            super(cause);
        }
    }
}
