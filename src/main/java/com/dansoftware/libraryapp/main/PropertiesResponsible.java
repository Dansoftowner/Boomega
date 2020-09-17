package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.log.LogFile;

/**
 * Responsible for adding the necessary system properties that are needed for the application.
 *
 * @author Daniel Gyorffy
 */
public final class PropertiesResponsible {

    public static final String LOG_FILE_PATH = "log.file.path";
    public static final String LOG_FILE_FULL_PATH = "log.file.path.full";

    /**
     * Puts the log file information into the system properties
     */
    private static void putLogFileProperties() {
        var logFile = new LogFile();
        System.setProperty(LOG_FILE_PATH, logFile.getAbsolutePath());
        System.setProperty(LOG_FILE_FULL_PATH, logFile.getPathWithExtension());
    }

    /**
     * Puts some javaFX information into the system properties
     */
    private static void putJFXProperties() {
        com.sun.javafx.runtime.VersionInfo.setupSystemProperties();
    }

    /**
     * Puts all the necessary system-properties that the application needs.
     */
    static void putSystemProperties() {
        putLogFileProperties();
        putJFXProperties();
    }

    private PropertiesResponsible() {
    }
}
