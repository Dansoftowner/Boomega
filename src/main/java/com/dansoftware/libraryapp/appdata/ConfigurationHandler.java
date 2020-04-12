package com.dansoftware.libraryapp.appdata;

import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can handle and store the application configurations
 *
 * @author Daniel Gyorffy
 */
public final class ConfigurationHandler {

    private static final Logger logger = Logger.getLogger(ApplicationDataFolder.class.getName());
    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    /**
     * Object, that represents the 'appdata' folder of the application
     */
    private ApplicationDataFolder applicationDataFolder;

    /**
     * The actual data holder object
     */
    private Properties properties = new Properties();

    /**
     * Don't let anyone to create an instance of this class
     */
    private ConfigurationHandler() {
        this.applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();

        putDefaultConfigurations();
        readConfigurations();
    }

    private void putDefaultConfigurations() {
        properties.put(PredefinedConfigurationKey.DEFAULT_LOCALE, "en");
    }

    /**
     * This method reads the configurations from the config file located in the application data folder
     * @return the data holder object
     */
    private void readConfigurations() {
        try (InputStream configFileReader = new BufferedInputStream(new FileInputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.loadFromXML(configFileReader);
        } catch (InvalidPropertiesFormatException e) {
            logger.log(Level.SEVERE, "The configuration file of the application couldn't be read", e);

            try {
                applicationDataFolder.createNewConfigurationFile();
            } catch (ApplicationDataFolder.UnableToCreateFileException ex) {
                throw new CouldntReadConfigurationsException(ex);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeConfigurations() throws IOException {
        try (OutputStream configFileWriter = new BufferedOutputStream(new FileOutputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.storeToXML(configFileWriter, null);
        }
    }

    public synchronized void setConfiguration(String key, String newValue) {
        this.properties.setProperty(key, newValue);
    }

    public synchronized void putConfiguration(String key, String value) {
        this.properties.put(key, value);
    }

    public synchronized String getConfiguration(String key) {
        return properties
                .get(key)
                .toString();
    }

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }

    public static final class CouldntReadConfigurationsException extends RuntimeException {
        CouldntReadConfigurationsException(Throwable e) {
            super("",e);
        }
    }
}
