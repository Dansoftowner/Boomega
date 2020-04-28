package com.dansoftware.libraryapp.appdata.config;

import java.util.*;

import static java.util.Objects.isNull;

public class ConfigurationPropertiesHolder implements ConfigurationHolder {

    private Properties data;

    public ConfigurationPropertiesHolder() {
    }

    @Override
    public <T> T getConfiguration(ConfigurationKey<T> key) {
        Objects.requireNonNull(key, "The 'key' argument must not be null"::toString);

        if (isNull(data)) this.data = new Properties();

        String keyAsString = key.toString();
        String configAsString = data.get(keyAsString).toString();

        return key.getTransformer().apply(configAsString);
    }

    @Override
    public <T> void setConfiguration(ConfigurationKey<T> key, T value) {
        putConfiguration(key, value);
    }

    @Override
    public void putConfigurations(Properties properties) {
        if (isNull(data)) this.data = properties;
        else loadFromProperties(properties);
    }

    @Override
    public void putConfiguration(String key, String value) {
        this.data.put(key, value);
    }

    private void loadFromProperties(Properties properties) {
        properties.forEach((var key, var value) -> this.data.put(key, value));
    }
}
