package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Objects;

/**
 * A ConfigurationXMLFileWriter can write configurations to an XML file
 * from a ConfigurationHolder object.
 */
public class ConfigurationXMLFileWriter implements ConfigurationWriter {

    private final File file;

    /**
     * Creates a normal xml file writer.
     *
     * @param file the file to write to
     * @throws NullPointerException if the file is null
     */
    public ConfigurationXMLFileWriter(File file) {
        this.file = Objects.requireNonNull(file, "The 'file' argument must not be null"::toString);
    }

    @Override
    public void writeConfigurations(ConfigurationHolder holder) throws IOException {
        try(OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(this.file))) {
            holder.toProperties().storeToXML(outputStream, null);
        }
    }
}
