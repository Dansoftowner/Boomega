package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.log.GuiLog;
import com.dansoftware.libraryapp.util.FileUtils;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A DefaultConfigurationFileReadingStrategy reads the configurations from the default configuration file
 * placed in the application data folder.
 *
 * <p>
 * This ReadingStrategy handles the exceptions that occurs during
 * the execution so it notifies the user about an unsuccessful
 * operation.
 */
public class DefaultConfigurationFileReadingStrategy extends XMLFileReadingStrategy {

    private static final Logger LOGGER = Logger.getLogger(DefaultConfigurationFileReadingStrategy.class.getName());

    public DefaultConfigurationFileReadingStrategy() {
        super(ApplicationDataFolderFactory.getApplicationDataFolder().getConfigurationFile());
    }

    @Override
    public void readConfigurationsTo(ConfigurationHolder holder) {
        try {
            super.readConfigurationsTo(holder);
        } catch (IOException e) {
            try {
                //if we couldn't read the configurations we try to create a new, blank one
                FileUtils.makeOldFileOf(getReadedFile());

                //notify the user about that the configurations couldn't be read and new configuration file created
                LOGGER.log(new GuiLog(Level.WARNING, e, "confighandler.newfile"));
            } catch (FileUtils.UnableToCreateFileException unableToCreateFileException) {
                /*
                  if the new file creation was unsuccessful,
                  we notify the user that the program
                  couldn't read the configurations
                */
                LOGGER.log(new GuiLog(Level.SEVERE, unableToCreateFileException, "confighandler.cantread"));
            }
        }
    }
}
