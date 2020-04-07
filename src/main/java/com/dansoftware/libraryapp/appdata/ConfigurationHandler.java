package com.dansoftware.libraryapp.appdata;

import java.io.*;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can handle the application configurations
 * @author Daniel Gyorffy
 */
@Deprecated
public final class ConfigurationHandler {

    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    /**
     * The logger that is used by this class
     */
    private static final Logger logger = Logger.getLogger(ApplicationDataFolder.class.getName());

    private ApplicationDataFolder applicationDataFolder;
    private Properties properties;

    private ConfigurationHandler() {
        this.applicationDataFolder = ApplicationDataFolderFactory.getConfigurationFolder();
        this.properties = readConfigurations();
    }

    private synchronized Properties readConfigurations() {
        properties = new Properties();
        try (InputStream configFileReader = new BufferedInputStream(new FileInputStream(applicationDataFolder.getConfigurationFile()))) {
            properties.loadFromXML(configFileReader);
        } catch (InvalidPropertiesFormatException e) {
            logger.log(Level.SEVERE, "The configuration file of the application couldn't be readed", e);
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

    public synchronized void putConfiguration(String key, String value) {
        this.properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized String getConfiguration(String key) {
        return properties.get(key).toString();
    }

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }
}
