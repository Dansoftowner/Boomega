package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Properties;

public class ConfigurationXMLFileWriter extends ConfigurationFileWriter {

    public ConfigurationXMLFileWriter(File file) {
        super(file);
    }

    @Override
    public void writeConfigurations(ConfigurationHolder holder) throws IOException {
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(getFile()))) {

        }
    }
}
