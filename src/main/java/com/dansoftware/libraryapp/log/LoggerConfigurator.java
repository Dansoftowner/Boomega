package com.dansoftware.libraryapp.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * This class is responsible for
 * configuring the necessary system
 * properties for the LogBack loggers.
 *
 * <p>
 *
 */
public final class LoggerConfigurator {

    private static final File LOG_FILE;

    static {
        String tmpDir = System.getProperty("java.io.tmpdir");
        String logFileName = "libraryapp";

        LOG_FILE = new File(tmpDir, logFileName);
    }

    /**
     * Sets the necessary system-properties for LogBack
     *
     * <p>
     * <b>Should be called before any logger is created</b>
     */
    public static void configure() {
        System.setProperty("logfile.path", LOG_FILE.getAbsolutePath());
    }

    /**
     * Gives access to the path of the log file.
     *
     * @return the absolute path of the log file
     */
    public static String getLogFilePath() {
        return LOG_FILE.getAbsolutePath() + ".log";
    }

}
