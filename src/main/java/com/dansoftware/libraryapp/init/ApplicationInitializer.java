package com.dansoftware.libraryapp.init;

import com.dansoftware.libraryapp.appdata.ConfigurationHandler;
import com.dansoftware.libraryapp.log.LoggerConfigurator;
import com.dansoftware.libraryapp.main.Main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Logger;

/**
 * This class is used to initialize some important thing
 * when the application starts.
 *
 * @see Main#init()
 * @author Daniel Gyorffy
 */
public final class ApplicationInitializer {

    private static boolean alreadyInstantiated;
    private static Logger logger = Logger.getLogger(ApplicationInitializer.class.getName());

    public ApplicationInitializer() {
        if (alreadyInstantiated)
            throw new UnsupportedOperationException("Application Initializer cannot be called more than once!");

        alreadyInstantiated = true;
    }

    @Step(step = 0)
    private void configureLogger() {
        LoggerConfigurator loggerConfigurator = LoggerConfigurator.getInstance();
        loggerConfigurator.configureRootLogger();
    }

    @Step(step = 1)
    @SuppressWarnings("all")
    private void readConfigurations() {
        ConfigurationHandler.getInstance();
    }

    public void init() {
        configureLogger();
        readConfigurations();
    }


    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    private @interface Step {
        int step();
    }
}
