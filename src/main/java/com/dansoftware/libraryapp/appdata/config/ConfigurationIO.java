package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;

public interface ConfigurationIO {

    /**
     * Reads the configurations into a {@link ConfigurationBase}
     * object.
     *
     * @return the {@link ConfigurationBase} object
     */
    ConfigurationBase read() throws IOException;

    /**
     * Writes the configuration to a particular target.
     */
    void write(ConfigurationBase configurationBase) throws IOException;
}
