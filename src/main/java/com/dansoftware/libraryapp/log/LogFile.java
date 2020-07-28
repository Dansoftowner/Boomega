package com.dansoftware.libraryapp.log;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Represents the log-file
 */
public class LogFile extends File {
    public LogFile() {
        super(FileUtils.getTempDirectory(), "libraryapp");
    }

    public String getPathWithExtension() {
        return super.getAbsolutePath().concat(".log");
    }

    @Override
    public String toString() {
        return super.getAbsolutePath();
    }
}
