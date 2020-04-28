package com.dansoftware.libraryapp.appdata;

/**
 * A ConfigurationReader can read configurations from a resource.
 */
public interface ConfigurationReader {

    /**
     * This method reads the configurations into a
     * configuration holder object.
     *
     * @param holder the holder object to read to; must not be null
     */
    void readConfigurations(ConfigurationHolder holder);
}
