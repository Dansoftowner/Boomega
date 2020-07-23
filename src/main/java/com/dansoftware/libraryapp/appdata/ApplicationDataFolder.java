package com.dansoftware.libraryapp.appdata;

import com.dansoftware.libraryapp.util.FileUtils;

import java.io.File;

/**
 * This class represents the Configuration folder of the application
 * which contains the configurations, default db file and the plugins folder.
 * To access these things located in the config folder, you can get them by calling
 * the right method of this class. This class can be instantiated only by the
 * {@link ApplicationDataFolderFactory} class.
 *
 * @author Daniel Gyorffy
 */
 @Deprecated
public abstract class ApplicationDataFolder {

    /**
     * Represents the folders/files in the application data folder
     */
    public enum ApplicationDataFolderElement {
        CONFIG_FILE("settings.configuration", Boolean.FALSE),
        DB_FILE("d0f0u0l0.lbadb", Boolean.FALSE),
        PLUGIN_FOLDER("plugins", Boolean.TRUE);

        /*-------------------------------------------->*/

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

    private final boolean firstCreated;

    /**
     * Package-private constructor
     */
    ApplicationDataFolder() {
        firstCreated = !getRootApplicationDataDirectory().exists();
    }

    /**
     * With this method, we can decide that the appdata folder created
     * after the start of this program, or it is already created before
     * the launch of the application.
     *
     * @return <code>true</code> if the application data folder created
     * after the launch of the program
     * <code>false</code> if the app data folder is already created before
     * the launch of the program
     */
    public boolean isFirstCreated() {
        return firstCreated;
    }

    /**
     * This method should return the root configurations folder
     * of the application.
     *
     * For example in Windows:
     * <em>C:\Users\<user>\Roaming\Dansoftware\libraryapp_2020</em>
     *
     * @return the file object representation of the directory
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
     * {@link ApplicationDataFolderElement} type,
     * and also creates the file on the disk.
     *
     * @param element the {@link ApplicationDataFolderElement} constant
     * @return {@link File} object that represents the given directory/file
     * from the Appdata folder
     */
    private File getFileOf(ApplicationDataFolderElement element) {
        File rootDirectory = FileUtils.createFile(getRootApplicationDataDirectory(), true);
        return FileUtils.createFile(new File(rootDirectory, element.fileName), element.directory);
    }
}
