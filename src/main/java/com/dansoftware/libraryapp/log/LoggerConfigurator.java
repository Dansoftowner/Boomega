package com.dansoftware.libraryapp.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

/**
 * This class configures the root logger of
 * the application
 *
 * @author Daniel Gyorffy
 */
public final class LoggerConfigurator {

    //private static final Logger LOGGER = Logger.getLogger(LoggerConfigurator.class.getName());


    private static final String EMPTY_STRING = "";

    /**
     * This field contains that the root logger already configured
     */
    private boolean configured;


    /**
     * Creates a basic logger configurator object
     */
    public LoggerConfigurator() {
    }

    /**
     * This method removes the default handlers from the root logger (ConsoleHandler)
     */
    private void removeDefaultHandlers() {
        LogManager.getLogManager().reset();
    }

    /**
     * Creates a file handler for the root logger
     * @return the created {@link FileHandler} object
     * @throws IOException if some I/O problem occurs
     */
    private FileHandler getFileHandler() throws IOException {
        logFile = File.createTempFile("libraryapp2020", ".log");

        FileHandler handler = new FileHandler(logFile.getAbsolutePath());
        handler.setLevel(Level.INFO);
        handler.setFormatter(new SimpleFormatter());

        return handler;
    }

    /**
     * @return the root logger
     */
    private Logger getRootLogger() {
        return LogManager.getLogManager().getLogger(EMPTY_STRING);
    }

    /**
     * This method configures the root logger
     */
    public void configureRootLogger() {

    }

    private void old0() {
        if (configured) return;

        Logger rootLogger = getRootLogger();

        try {
            removeDefaultHandlers();

            rootLogger.addHandler(getFileHandler());
            rootLogger.addHandler(new GuiHandler());

            configured = Boolean.TRUE;
        } catch (IOException e) {
         //   LOGGER.log(Level.WARNING, "Couldn't create FileHandler for root logger", e);
        }
    }

    /**
     * @return the file that the application logs to
     */
    public static File getLogFile() {
        return logFile;
    }

}
