package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Objects;
import java.util.Properties;

public class ConfigurationXMLFileReader extends ConfigurationFileReader {

    public ConfigurationXMLFileReader(File file) {
        super(file);
    }

    @Override
    public void readConfigurationsTo(ConfigurationHolder holder) throws IOException {
        Objects.requireNonNull(holder, "The 'holder' argument must not be null"::toString);

        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(getFile()))) {
            Properties properties = new Properties();
            properties.loadFromXML(inputStream);
            holder.putConfigurations(properties);
        }
    }
}
