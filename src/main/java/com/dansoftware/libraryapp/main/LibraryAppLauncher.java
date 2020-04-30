package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.ExceptionUtils;
import com.dansoftware.libraryapp.log.LoggerConfigurator;
import com.sun.javafx.application.LauncherImpl;

import java.util.Locale;

/**
 * The Main-class of the application
 */
public class LibraryAppLauncher {

    static {
        //Configure the logger
        new LoggerConfigurator()
                .configureRootLogger();

        //set the default locale
        Locale.setDefault(Locale.ENGLISH);

        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(ExceptionUtils.DEFAULT_EXCEPTION_HANDLER);
    }

    /**
     * The main-method of the application;
     *
     * <p>
     * Parses the application argument(s)
     * and launch the application with a
     * preloader.
     *
     * @see ApplicationArgumentHandler
     * @see LauncherImpl#launchApplication(Class, Class, String[])
     */
    public static void main(String[] args) {
        new ApplicationArgumentHandler(args);

        LauncherImpl.launchApplication(GuiApplicationStarter.class, Preloader.class, null);
    }
}
