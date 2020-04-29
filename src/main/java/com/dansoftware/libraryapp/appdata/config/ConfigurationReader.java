package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;

/**
 * A ConfigurationReader can read configurations from a resource.
 */
public interface ConfigurationReader {

    /**
     * Reads the configurations into a
     * configuration holder object.
     *
     * @param holder the holder object to read to; must not be null
     * @throws NullPointerException if the {@param holder} is null
     * @throws IOException if some I/O exception occurs during the reading
     */
    void readConfigurationsTo(ConfigurationHolder holder) throws IOException;
}
