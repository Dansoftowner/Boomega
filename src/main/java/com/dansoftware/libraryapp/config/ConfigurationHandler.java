package com.dansoftware.libraryapp.config;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * This class can handle the application configurations
 * @author Daniel Gyorffy
 */
public final class ConfigurationHandler {

    private static final ConfigurationHandler INSTANCE = new ConfigurationHandler();

    private File configurationFile;
    private Properties properties;

    private ConfigurationHandler() {
        ConfigurationFolderFactory configurationFolderFactory = new ConfigurationFolderFactory();
        ConfigurationFolder configurationFolder = configurationFolderFactory.getConfigurationFolder();

        this.configurationFile = Objects.requireNonNull(configurationFolder).getConfigurationFile();
        this.properties = readConfigurations(configurationFile);
    }

    private synchronized Properties readConfigurations(File configurationFile) {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(configurationFile)))) {
            return (Properties) objectInputStream.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeConfigurations() throws IOException {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(configurationFile)))) {
            objectOutputStream.writeObject(properties);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
