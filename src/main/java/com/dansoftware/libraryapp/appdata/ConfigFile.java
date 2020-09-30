package com.dansoftware.libraryapp.appdata;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class ConfigFile extends File {

    private static final ConfigFile instance = new ConfigFile();

    private final boolean nonExisted;

    private ConfigFile() {
        super(new File(FileUtils.getUserDirectoryPath(), ".libraryapp2020"), "config.conf");
        nonExisted = !exists();
    }

    public boolean isNonExisted() {
        return nonExisted;
    }

    public static ConfigFile getConfigFile() {
        return instance;
    }
}
