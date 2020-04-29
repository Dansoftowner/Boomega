package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * A ConfigurationXMLFileReader can read and parse XML-configuration files
 * and store the read configurations into a {@link ConfigurationHolder} object.
 */
public class ConfigurationXMLFileReader implements ConfigurationReader {

    private final File file;

    /**
     * Creates a normal xml file reader.
     *
     * @param file the file to read from
     * @throws NullPointerException if the file is null
     */
    public ConfigurationXMLFileReader(File file) {
        this.file = Objects.requireNonNull(file, "The 'file' argument must not be null"::toString);
    }

    @Override
    public void readConfigurationsTo(ConfigurationHolder holder) throws IOException {
        Objects.requireNonNull(holder, "The 'holder' argument must not be null"::toString);

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(this.file))) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            holder.putConfigurations(properties);
        }
    }
}
