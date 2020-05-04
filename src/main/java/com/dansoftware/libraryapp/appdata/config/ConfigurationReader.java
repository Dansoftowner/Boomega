package com.dansoftware.libraryapp.appdata.config;

import java.io.IOException;
import java.util.Objects;

/**
 * A ConfigurationReader can read configurations from a resource.
 */
public class ConfigurationReader {

    private final ReadingStrategy readingStrategy;

    public ConfigurationReader(ReadingStrategy readingStrategy) {
        this.readingStrategy = Objects.requireNonNull(readingStrategy, "The reading strategy mustn't be null"::toString);
    }

    /**
     * Reads the configurations into a
     * configuration holder object.
     *
     * @param holder the holder object to read to; must not be null
     * @throws NullPointerException if the {@param holder} is null
     * @throws IOException if some I/O exception occurs during the reading
     */
    public void readConfigurationsTo(ConfigurationHolder holder) throws IOException {
        readingStrategy.readConfigurationsTo(holder);
    }
}
