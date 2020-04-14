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
public final class ConfigurationHandler {

    private static final Logger logger = Logger.getLogger(ApplicationDataFolder.class.getName());
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

    private void putDefaultConfigurations() {
        properties.put(PredefinedConfigurationKey.DEFAULT_LOCALE, "en");
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
                logger.log(new GuiLog(Level.WARNING, "confighandler.newfile", ex));
            } catch (ApplicationDataFolder.UnableToCreateFileException unableToCreateFileException) {
                logger.log(new GuiLog(Level.SEVERE, "confighandler.cantread", unableToCreateFileException));
            }
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
}
