package com.dansoftware.libraryapp.db.util;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.config.ConfigurationKey;
import com.dansoftware.libraryapp.db.DefaultDBConnection;
import com.dansoftware.libraryapp.main.ApplicationArgumentHandler;
import com.dansoftware.libraryapp.main.Globals;

import java.io.File;
import java.util.Optional;

import static com.dansoftware.libraryapp.main.Globals.getConfigurationHolder;

/**
 * This class is responsible for recognizing the right database file
 * considerate the application parameters and the user settings.
 *
 * @author Daniel Gyorffy
 * @see DefaultDBConnection
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
     * @see Globals#getConfigurationHolder()
     * @see ConfigurationKey#CUSTOM_DB_FILE
     * @see ApplicationDataFolder#getDefaultDatabaseFile
     */
    public final File getDBFile() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        Optional<File> launchedFile = ApplicationArgumentHandler.getLaunchedFile();
        Optional<File> customConfiguredDB =
                Optional.ofNullable(getConfigurationHolder().getConfiguration(ConfigurationKey.CUSTOM_DB_FILE));

        return launchedFile.orElseGet(() -> customConfiguredDB.orElseGet(applicationDataFolder::getDefaultDatabaseFile));
    }
}
