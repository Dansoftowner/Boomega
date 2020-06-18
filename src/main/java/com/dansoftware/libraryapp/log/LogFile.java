package com.dansoftware.libraryapp.log;

import java.io.File;

/**
 * Represents the log-file
 */
public class LogFile extends File {
    public LogFile() {
        super(System.getProperty("java.io.tmpdir"), "libraryapp");
    }

    public String getPathWithExtension() {
        return super.getAbsolutePath() + ".log";
    }

    @Override
    public String toString() {
        return super.getAbsolutePath();
    }
}
