package com.dansoftware.libraryapp.appdata.config;

import java.util.Properties;

/**
 * A ConfigurationHolder can store the application configurations in the memory
 * and allows to access these application settings/configurations by a {@link ConfigurationKey}.
 */
public interface ConfigurationHolder {

    /**
     * This method returns the configuration-value by the configuration-key
     *
     * @param key the key of the configuration; must not be null
     * @param <T> the type of the configuration-value;
     *            depends on what ConfigurationKey passed as an argument
     * @return the value of the particular configuration
     */
    <T> T getConfiguration(ConfigurationKey<T> key);

    /**
     * This method returns the default configuration specified by the key object.
     *
     * @param key the key of the configuration
     * @param <T> the type of the configuration-value;
     *            depends on what ConfigurationKey passed as an argument
     * @return the default value of the particular configuration
     * @see ConfigurationKey#getDefaultValue()
     */
    default <T> T getDefaultConfiguration(ConfigurationKey<T> key) {
        return key.getDefaultValue();
    }

    /**
     * This method can be used to put/create some configuration
     *
     * @param key the key of the configuration
     * @param value the value of the configuration
     * @param <T> the type of the configuration-value;
     *            depends on what ConfigurationKey passed as an argument
     * @see ConfigurationHolder#putConfiguration(String, String)
     */
    default <T> void putConfiguration(ConfigurationKey<T> key, T value) {
        this.putConfiguration(key.toString(), value.toString());
    }

    /**
     * Puts configuration by plain String values.
     *
     * @param key the configuration-key as String; must not be null
     * @param value the configuration-value as String
     * @throws NullPointerException if the {@param key} is null
     */
    void putConfiguration(String key, String value);

    /**
     * This method should be used if we want to modify some
     * configuration
     *
     * @param key the key of the configuration
     * @param value the new configuration
     * @param <T> the type of the configuration-value;
     *           depends on what ConfigurationKey passed as an argument
     */
    <T> void setConfiguration(ConfigurationKey<T> key, T value);

    /**
     * Reads the configurations from a {@link Properties} object.
     *
     * @param properties must not be null;
     * @throws NullPointerException if the {@param properties} is null.
     */
    void putConfigurations(Properties properties);

}
