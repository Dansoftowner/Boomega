package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;

/**
 * A ConfigurationWriter can write configurations from a {@link ConfigurationHolder} object
 * to a particular target (for example: a File).
 */
public interface ConfigurationWriter {

    /**
     * Writes the configurations from a {@link ConfigurationHolder}
     * object to a target.
     *
     * <p>
     *  The type of the particular target depends
     *  on what {@link ConfigurationWriter} used
     *  to execute this task.
     *
     * @param holder to be wrote; must not be null
     * @throws NullPointerException if the {@param holder} is null
     * @throws IOException if some I/O exception occurs during the file writing
     */
    void writeConfigurations(ConfigurationHolder holder) throws IOException;
}
