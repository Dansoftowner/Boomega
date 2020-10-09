package com.dansoftware.libraryapp.appdata;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ConfigFile extends File {

    private static ConfigFile defaultFile;

    private final boolean nonExisted;

    public ConfigFile() {
        super(new File(FileUtils.getUserDirectoryPath(), ".libraryapp2020"), "config.conf");
        nonExisted = !determineExists();
    }

    public boolean isNonExisted() {
        return nonExisted;
    }

    protected boolean determineExists() {
        return exists();
    }

    protected void create() throws IOException {
        getParentFile().mkdirs();
        createNewFile();
    }

    public InputStream openStream() throws IOException {
        if (determineExists()) create();
        return new FileInputStream(this);
    }

    public static void setDefault(@NotNull ConfigFile configFile) {
        defaultFile = Objects.requireNonNull(configFile);
    }

    public static ConfigFile getConfigFile() {
        if (defaultFile == null)
            defaultFile = new ConfigFile();
        return defaultFile;
    }
}
