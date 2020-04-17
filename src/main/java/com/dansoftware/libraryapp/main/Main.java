package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.ExceptionUtils;
import com.dansoftware.libraryapp.log.LoggerConfigurator;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.*;

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
     * This field contains the primary window of the Window Hierarchy
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
     * MAIN
     */
    public static void main(String[] args) {
        new ApplicationArgumentHandler(args);

        LauncherImpl.launchApplication(Main.class, null);
    }

    @Override
    public void init() {
        new ApplicationInitializer().init();
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
     *
     * If the application already started the {@link Runnable#run}
     * method will be executed immediately.
     *
     * @param runnable the thing that should be executed
     * @see Main#start
     */
    public synchronized static void runAfterStart(Runnable runnable) {
        //If the application already started
        if (alreadyStarted()) runnable.run(); else runAfterStart.add(runnable);
    }

    /**
     * Returns that the application already started
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
