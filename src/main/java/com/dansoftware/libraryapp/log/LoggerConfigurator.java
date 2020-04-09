package com.dansoftware.libraryapp.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * This class deals with the log
 * settings of the application
 *
 * @author Daniel Gyorffy
 */
public final class LoggerConfigurator {

    private static final LoggerConfigurator INSTANCE = new LoggerConfigurator();
    private static final Logger logger = Logger.getLogger(LoggerConfigurator.class.getName());
    private static File logFile;

    /**
     * Don't let anyone to create an instance of this class
     */
    private LoggerConfigurator() {
    }

    /**
     * This method removes the default handlers from the root logger
     */
    private void removeDefaultHandlers() {
        LogManager logManager = LogManager.getLogManager();
        logManager.reset();
    }

    private FileHandler getFileHandler() throws IOException {
        logFile = File.createTempFile("libraryapp2020", ".log");

        return new FileHandler(logFile.getAbsolutePath());
    }

    /**
     * @return the root logger
     */
    private Logger getRootLogger() {
        return LogManager.getLogManager().getLogger("");
    }

    /**
     * This method configures the root logger
     */
    public void configureRootLogger() {
        Logger rootLogger = getRootLogger();

        try {

            FileHandler fileHandler = getFileHandler();
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());

            removeDefaultHandlers();

            rootLogger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Couldn't create FileHandler for root logger", e);
        }
    }

    /**
     * @return the log file of the program
     */
    public static File getLogFile() {
        return logFile;
    }

    public static LoggerConfigurator getInstance() {
        return INSTANCE;
    }
}
