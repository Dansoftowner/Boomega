package com.dansoftware.libraryapp.appdata;

/**
 * A ConfigurationWriter can write configurations from a {@link ConfigurationHolder} object
 * to a particular target (for example: a File).
 */
public interface ConfigurationWriter {

    /**
     * This method writes the configurations from a {@link ConfigurationHolder}
     * object to a particular target.
     *
     * @param holder to be wrote; must not be null
     */
    void writeConfigurations(ConfigurationHolder holder);
}
