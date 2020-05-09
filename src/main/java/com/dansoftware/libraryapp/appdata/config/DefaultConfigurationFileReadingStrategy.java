package com.dansoftware.libraryapp.appdata.config;

import com.dansoftware.libraryapp.appdata.ApplicationDataFolderFactory;
import com.dansoftware.libraryapp.gui.notification.Notification;
import com.dansoftware.libraryapp.gui.notification.NotificationLevel;
import com.dansoftware.libraryapp.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigurationFileReadingStrategy.class);

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

                Notification.create()
                        .level(NotificationLevel.WARNING)
                        .msg("config.handler.read.warning")
                        .show();

                LOGGER.warn("The configuration file couldn't be read. New configuration file created");
            } catch (FileUtils.UnableToCreateFileException unableToCreateFileException) {

                //if the new file creation was unsuccessful,
                //we notify the user that the program
                //couldn't read the configurations

                Notification.create()
                        .level(NotificationLevel.ERROR)
                        .msg("config.handler.read.error")
                        .cause(unableToCreateFileException)
                        .show();

                LOGGER.warn("The configuration file couldn't be read", unableToCreateFileException);
            }
        }
    }
}
