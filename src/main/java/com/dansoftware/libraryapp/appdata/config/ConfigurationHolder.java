package com.dansoftware.libraryapp.appdata.config;

import java.util.Properties;

/**
 * A ConfigurationHolder can store the application configurations in the memory
 * and allows to access them.
 *
 * <p>
 * A Configuration Holder is 100% interoperable with a {@link Properties}
 * object. We can convert a ConfigurationHolder object to a Properties object
 * and vice versa.
 *
 * @author Daniel Gyorffy
 */
public interface ConfigurationHolder {

    /**
     * This method returns the configuration-value by the configuration-key
     *
     * <p>
     * If there is no such configuration specified by the given key,
     * this method will automatically return the default value of that
     * key.
     *
     * @param key the key of the configuration; must not be null
     * @param <T> the type of the configuration-value;
     *            depends on what ConfigurationKey passed as an argument
     * @return the value of the particular configuration
     * @see ConfigurationHolder#getDefaultConfiguration(ConfigurationKey)
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
     * This method can be used to put/create some configuration.
     *
     * <p>
     * Automatically calls the {@link ConfigurationHolder#putConfiguration(String, String)}
     * method with the toSting() version of the key argument and the toString()
     * version of the value argument.
     * </p>
     *
     * @param key   the key of the configuration
     * @param value the value of the configuration
     * @param <T>   the type of the configuration-value;
     *              depends on what ConfigurationKey passed as an argument
     * @see ConfigurationHolder#putConfiguration(String, String)
     */
    default <T> void putConfiguration(ConfigurationKey<T> key, T value) {
        this.putConfiguration(key.toString(), value.toString());
    }

    /**
     * Puts a configuration by plain String values.
     *
     * @param key   the configuration-key as String; must not be null
     * @param value the configuration-value as String
     * @throws NullPointerException if the key is null
     */
    void putConfiguration(String key, String value);

    /**
     * This method should be used if we want to modify some
     * configuration
     *
     * @param key   the key of the configuration; must not be null
     * @param value the new configuration
     * @param <T>   the type of the configuration-value;
     *              depends on what ConfigurationKey passed as an argument
     * @throws NullPointerException if the key is null
     */
    <T> void setConfiguration(ConfigurationKey<T> key, T value);

    /**
     * Removes the configuration from the memory by the specified key.
     *
     * @param configurationKey the key of the configuration; must not be null
     * @throws NullPointerException if the key is null
     */
    void removeConfiguration(ConfigurationKey<?> configurationKey);

    /**
     * Removes all configurations with the specified keys.
     *
     * <p>
     * Iterates over the given keys and calls the {@link ConfigurationHolder#removeConfiguration}
     * with each key
     *
     * @param keys the array of
     */
    default void removeConfigurations(ConfigurationKey<?>... keys) {
        for (ConfigurationKey<?> key : keys) removeConfiguration(key);
    }

    /**
     * Reads the configurations from a {@link Properties} object.
     * Basically we can convert a properties object to a
     * ConfigurationHolder object with this method.
     *
     * @param properties must not be null;
     * @throws NullPointerException if the {@param properties} is null.
     */
    void putConfigurations(Properties properties);


    /**
     * Converts a ConfigurationHolder object to a Properties object
     *
     * @return the Properties-object version of the object
     */
    Properties toProperties();

}
