package com.dansoftware.libraryapp.appdata.config;

import java.util.*;

import static java.util.Objects.isNull;

/**
 * A ConfigurationHashtableHolder stores the configurations within a Hashtable
 * object <i>specifically a {@link Properties} object</i>
 *
 * @author Daniel Gyorffy
 */
public class ConfigurationHashtableHolder implements ConfigurationHolder {

    private Properties data;

    public ConfigurationHashtableHolder() {
    }

    @Override
    public <T> T getConfiguration(ConfigurationKey<T> key) {
        Objects.requireNonNull(key, "The 'key' argument must not be null"::toString);

        if (isNull(data)) this.data = new Properties();

        String keyAsString = key.toString();

        Object configuration;
        if (isNull(configuration = data.get(keyAsString))) {
            return getDefaultConfiguration(key);
        }

        String configAsString = configuration.toString();

        return key.getTransformer().apply(configAsString);
    }

    @Override
    public <T> void setConfiguration(ConfigurationKey<T> key, T value) {
        putConfiguration(key, value);
    }

    @Override
    public void removeConfiguration(ConfigurationKey<?> configurationKey) {
        this.data.remove(configurationKey.toString());
    }

    @Override
    public void putConfigurations(Properties properties) {
        if (isNull(data)) this.data = properties;
        else loadFromProperties(properties);
    }

    @Override
    public Properties toProperties() {
        return data;
    }

    @Override
    public void putConfiguration(String key, String value) {
        this.data.put(key, value);
    }

    private void loadFromProperties(Properties properties) {
        properties.forEach((var key, var value) -> this.data.put(key, value));
    }
}
