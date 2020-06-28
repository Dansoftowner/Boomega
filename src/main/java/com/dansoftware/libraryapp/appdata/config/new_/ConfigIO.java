package com.dansoftware.libraryapp.appdata.config.new_;

import java.io.IOException;

public interface ConfigIO {

    /**
     * Reads all configurations into an {@link AppConfig} object.
     *
     * @return the {@link AppConfig} object.
     */
    AppConfig read() throws IOException;

    /**
     * Writes all configurations.
     *
     * @param appConfig the {@link AppConfig} object that holds the configurations in the memory;
     *                  mustn't be null
     * @throws NullPointerException if appConfig is null
     */
    void write(AppConfig appConfig) throws IOException;
}
