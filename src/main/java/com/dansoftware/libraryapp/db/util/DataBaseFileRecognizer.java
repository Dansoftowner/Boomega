package com.dansoftware.libraryapp.db.util;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.appdata.PredefinedConfiguration;
import com.dansoftware.libraryapp.db.DBConnection;
import com.dansoftware.libraryapp.main.ApplicationArgumentHandler;

import java.io.File;
import java.util.Optional;

/**
 * This class is responsible for recognizing the right database file
 * considerate the application parameters and the user settings.
 *
 * @author Daniel Gyorffy
 */
public final class DataBaseFileRecognizer {

    /**
     * This method checks the application settings and the application parameters
     * to decide the right database file, and then returns it.
     *
     * The database file priority:
     * <ol>
     *    <li>The launched file that passed as an application argument</li>
     *    <li>The database file that configured by the user</li>
     *    <li>The default database file in the Application data folder</li>
     * </ol>
     *
     * @see ApplicationArgumentHandler#getLaunchedFile
     * @see ConfigurationHandler#getConfiguration
     * @see PredefinedConfiguration#CUSTOM_DB_FILE
     * @see ApplicationDataFolder#getDefaultDatabaseFile
     */
    public final File getDBFile() {
        ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        Optional<File> launchedFile = ApplicationArgumentHandler.getLaunchedFile();
        Optional<String> customConfiguredDB =
                Optional.ofNullable(configurationHandler.getConfiguration(PredefinedConfiguration.CUSTOM_DB_FILE.getKey()));

        return launchedFile.orElseGet(() -> customConfiguredDB.map(File::new).orElseGet(applicationDataFolder::getDefaultDatabaseFile));
    }
}
