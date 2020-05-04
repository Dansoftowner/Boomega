package com.dansoftware.libraryapp.appdata.config;

import java.io.*;
import java.util.Objects;

/**
 * An XMLFileWritingStrategy writes the configurations to an XML file
 */
public class XMLFileWritingStrategy implements WritingStrategy {

    private final File file;

    public XMLFileWritingStrategy(File file) {
        this.file = Objects.requireNonNull(file, "The 'file' argument must not be null"::toString);
    }

    @Override
    public void writeConfigurations(ConfigurationHolder holder) throws IOException {
        try(var outputStream = new BufferedOutputStream(new FileOutputStream(this.file))) {
            holder.toProperties().storeToXML(outputStream, null);
        }
    }
}
