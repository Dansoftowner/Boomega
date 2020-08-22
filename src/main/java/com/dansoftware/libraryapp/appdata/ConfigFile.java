package com.dansoftware.libraryapp.appdata;

import org.apache.commons.io.FileUtils;

import java.io.File;

class ConfigFile extends File {

    private static final String DIR_NAME = ".libraryapp2020";
    private static final String FILE_NAME = "config.conf";

    ConfigFile() {
        super(new File(FileUtils.getUserDirectoryPath(), DIR_NAME), FILE_NAME);
    }
}
