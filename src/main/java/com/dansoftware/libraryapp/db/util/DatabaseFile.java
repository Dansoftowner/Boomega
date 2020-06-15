package com.dansoftware.libraryapp.db.util;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolder;
import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.appdata.config.ConfigurationBase;
import com.dansoftware.libraryapp.main.ApplicationArgumentHandler;

import java.io.File;
import java.util.Optional;

/**
 * This class is responsible for recognizing the right database file
 * considerate the application parameters and the user settings.
 *
 * @author Daniel Gyorffy
 */
public final class DatabaseFile {

    private final String path;

    public DatabaseFile() {
        ApplicationDataFolder applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        Optional<String> launchedFile = ApplicationArgumentHandler.getLaunchedFile().map(File::toString);

        if (launchedFile.isEmpty()) {
            this.path = ConfigurationBase.getGlobal().getLoggedAccount().getFilePath();
        } else {
            this.path = launchedFile.get();
        }
    }

    @Override
    public String toString() {
        return path;
    }
}
