package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.ExceptionUtils;
import com.dansoftware.libraryapp.log.LoggerConfigurator;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Main class of the application
 *
 * @author Daniel Gyorffy
 */
public class Main extends Application {

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
     * Contains the primary window of the Window Hierarchy
     * Should be initialized by the start() method
     *
     * @see Main#start(Stage)
     */
    private static Stage primaryStage;

    /**
     * This list contains all the tasks that are should be executed after
     * the application finally starts
     *
     * @see Main#start
     */
    private static List<Runnable> runAfterStart = new LinkedList<>();

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

        LauncherImpl.launchApplication(Main.class, Preloader.class, null);
    }

    @Override
    public void init() {
        ApplicationInitializer initializer = new ApplicationInitializer();
        initializer.initializeApplication();
    }


    @Override
    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;

        runAfterStart.forEach(Runnable::run);
        runAfterStart = null;
    }

    @Override
    public void stop() {
    }

    /**
     * This method allows us to define tasks
     * that are must be executed after the application
     * starts.
     * <p>
     * If the application already started the {@link Runnable#run}
     * method will be executed immediately.
     *
     * @param runnable the thing that should be executed
     * @see Main#start
     */
    public synchronized static void runAfterStart(Runnable runnable) {
        //If the application already started
        if (alreadyStarted()) runnable.run();
        else runAfterStart.add(runnable);
    }

    /**
     * Returns that the application already started
     *
     * @return <code>true</code> if the application already started
     * <code>false</code> otherwise.
     */
    private static boolean alreadyStarted() {
        return runAfterStart == null;
    }

    /**
     * @return the primary stage of the window hierarchy
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
