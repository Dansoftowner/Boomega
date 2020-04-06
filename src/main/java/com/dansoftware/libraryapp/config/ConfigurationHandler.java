package com.dansoftware.libraryapp.config;

import java.io.*;
import java.util.Objects;
import java.util.Properties;
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
    private static final Logger logger = Logger.getLogger(ConfigurationFolder.class.getName());


    private ConfigurationFolder configurationFolder;
    private Properties properties;

    private ConfigurationHandler() {
        ConfigurationFolderFactory configurationFolderFactory = new ConfigurationFolderFactory();

        this.configurationFolder = configurationFolderFactory.getConfigurationFolder();
        this.properties = readConfigurations();
    }

    private synchronized Properties readConfigurations() {
        properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader(configurationFolder.getConfigurationFile())));
        } catch (IOException e) {

        }

/*        try(ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(configurationFile)))) {
            return (Properties) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/

return null;
    }

    public synchronized void writeConfigurations() throws IOException {
/*        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(configurationFile)))) {
            objectOutputStream.writeObject(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }*/
    }

    public synchronized<T extends Serializable> void putConfiguration(T key, T value) {
        this.properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T getConfiguration(String key) {
        return (T) properties.get(key);
    }

    public static ConfigurationHandler getInstance() {
        return INSTANCE;
    }
}
