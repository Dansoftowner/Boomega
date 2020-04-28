package com.dansoftware.libraryapp.appdata.config;

import java.io.File;
import java.util.Objects;

public abstract class ConfigurationFileReader implements ConfigurationReader {

    private File file;

    public ConfigurationFileReader(File file) {
        this.file = Objects.requireNonNull(file, "The 'file' argument must not be null"::toString);
    }

    public File getFile() {
        return file;
    }
}
