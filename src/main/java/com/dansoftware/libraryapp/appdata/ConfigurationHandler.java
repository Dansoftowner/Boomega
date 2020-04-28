package com.dansoftware.libraryapp.appdata;

import com.dansoftware.libraryapp.log.GuiLog;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * This class can handle and store the application configurations
 *
 * @author Daniel Gyorffy
 */
@Deprecated
public final class ConfigurationHandler {

    private static final Logger LOGGER = Logger.getLogger(ApplicationDataFolder.class.getName());

    /**
     * This constant contains the single instance of this class
     */
    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    /**
     * Object, that represents the 'appdata' folder of the application
     * Used for accessing the configuration file
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

    /**
     * This method puts the default configurations/settings to the properties object
     */
    private void putDefaultConfigurations() {
        for (PredefinedConfiguration predefinedConfiguration : PredefinedConfiguration.values()) {
            properties.put(predefinedConfiguration.getKey(), predefinedConfiguration.getDefaultValue());
        }
    }

    /**
     * This method reads the configurations from the config file located in the application data folder
     *
     * If the configuration file cannot be read for some reason, this method tries to create a new
     * configuration file
     *
     * @return the data holder object
     */
    private void readConfigurations() {
        try (InputStream configFileReader = new BufferedInputStream(new FileInputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.loadFromXML(configFileReader);
        } catch (IOException ex) {
            try {
                applicationDataFolder.createNewConfigurationFile();
                LOGGER.log(new GuiLog(Level.WARNING, "confighandler.newfile", ex));
            } catch (ApplicationDataFolder.UnableToCreateFileException unableToCreateFileException) {
                LOGGER.log(new GuiLog(Level.SEVERE, "confighandler.cantread", unableToCreateFileException));
            }
        }
    }

    /**
     * With this method, we can write the currently adjusted settings/configurations to
     * the configuration file on the disk
     * @throws IOException if som I/O problem occurs
     */
    public synchronized void writeConfigurations() throws IOException {
        try (OutputStream configFileWriter = new BufferedOutputStream(new FileOutputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.storeToXML(configFileWriter, null);
        }
    }

    /**
     * This method should be used if we want to modify some
     * configuration
     * @param key the key of the configuration
     * @param newValue the new configuration
     */
    public synchronized void setConfiguration(String key, String newValue) {
        this.properties.setProperty(key, newValue);
    }

    /**
     * This method can be used to put/create some configuration
     * @param key the key of the configuration
     * @param value the value of the configuration
     */
    public synchronized void putConfiguration(String key, String value) {
        this.properties.put(key, value);
    }

    /**
     * This method return the configuration of the specified key
     * @param key the key of the configuration
     * @return the value of the configuration
     */
    public synchronized String getConfiguration(String key) {
        return properties.get(key).toString();
    }

    /**
     * @return the single instance of this class
     */
    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }
}
