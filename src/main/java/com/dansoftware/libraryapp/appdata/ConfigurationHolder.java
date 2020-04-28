package com.dansoftware.libraryapp.appdata;

/**
 * A ConfigurationHolder can store the application configurations in the memory
 * and allows to access these application settings/configurations by a {@link ConfigurationKey}.
 */
public interface ConfigurationHolder {

    /**
     * This method returns the configuration-value of the configuration-key
     *
     * @param key the key of the configuration; must not be null
     * @param <T> the type of the configuration-value
     * @return the value of the particular configuration
     */
    <T> T getConfiguration(ConfigurationKey<T> key);

    /**
     * This method returns the default configuration specified by the key object.
     *
     * @param key the key of the configuration
     * @param <T> the type of the configuration-value
     * @return the default value of the particular configuration
     */
    <T> T getDefaultConfiguration(ConfigurationKey<T> key);

    /**
     * This method can be used to put/create some configuration
     *
     * @param key the key of the configuration
     * @param value the value of the configuration
     * @param <T> the type of the configuration-value
     */
    <T> void putConfiguration(ConfigurationKey<T> key, T value);

    /**
     * This method should be used if we want to modify some
     * configuration
     *
     * @param key the key of the configuration
     * @param value the new configuration
     * @param <T> the type of the configuration-value
     */
    <T> void setConfiguration(ConfigurationKey<T> key, T value);
}
