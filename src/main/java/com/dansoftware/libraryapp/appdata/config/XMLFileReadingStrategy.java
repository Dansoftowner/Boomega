package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

/**
 * An XMLFileReadingStrategy reads the configurations from an XML file
 */
public class XMLFileReadingStrategy implements ReadingStrategy {

    private final File file;

    public XMLFileReadingStrategy(File file) {
        this.file = Objects.requireNonNull(file, "The 'file' argument must not be null"::toString);
    }

    protected File getReadedFile() {
        return file;
    }

    @Override
    public void readConfigurationsTo(ConfigurationHolder holder) throws IOException {
        Objects.requireNonNull(holder, "The 'holder' argument must not be null"::toString);

        try(var reader = new BufferedReader(new FileReader(file))) {
            Properties properties = new Properties();
            properties.load(reader);
            holder.putConfigurations(properties);
        }
    }
}
