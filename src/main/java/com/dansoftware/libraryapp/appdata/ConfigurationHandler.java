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
    private Properties properties;

    /**
     * Don't let anyone to create an instance of this class
     */
    private ConfigurationHandler() {
        this.applicationDataFolder = ApplicationDataFolderFactory.getApplicationDataFolder();
        this.properties = readConfigurations();
    }

    /**
     * This method reads the configurations from the config file located in the application data folder
     * @return the data holder object
     */
    private synchronized Properties readConfigurations() {
        var properties = new Properties();
        try (InputStream configFileReader = new BufferedInputStream(new FileInputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.loadFromXML(configFileReader);
        } catch (InvalidPropertiesFormatException e) {
            logger.log(Level.SEVERE, "The configuration file of the application couldn't be read", e);

            applicationDataFolder.createNewConfigurationFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
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
}
