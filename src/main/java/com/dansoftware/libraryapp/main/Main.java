package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.EntryPoint;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.main.init.ApplicationArgumentHandler;
import com.dansoftware.libraryapp.main.init.ApplicationInitializer;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * The main class and javafx application starter.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class Main extends Application {

    static {
        //Configure the logger
        var logFile = new LogFile();
        System.setProperty("log.file.path", logFile.getAbsolutePath());
        System.setProperty("log.file.path.full", logFile.getPathWithExtension());

        //set the initial locale
        Locale.setDefault(Locale.ENGLISH);

        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
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
        ApplicationArgumentHandler.scan(args);

        LauncherImpl.launchApplication(Main.class, Preloader.class, null);
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

    @Override
    public void init() {
        ApplicationInitializer initializer = new ApplicationInitializer();
        initializer.initializeApplication();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage;

        EntryPoint entryPoint = new EntryPoint(primaryStage,true);
        boolean show = entryPoint.show();

        if (!show) Platform.exit();

//        Theme.applyDefault(scene);
//
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("LibraryApp");
//        primaryStage.setFullScreenExitHint(getGeneralWord("window.fullscreen.hint"));
//        primaryStage.getIcons().add(Globals.WINDOW_ICON);
//        primaryStage.initStyle(StageStyle.DECORATED);
//        primaryStage.setOnCloseRequest(null);
//        primaryStage.setResizable(true);
//        primaryStage.setMaximized(true);
//        primaryStage.show();

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
     * Gives access to the primary stage (main window) through an Optional.
     *
     * @return the Optional of the primary stage
     */
//    public static Optional<Stage> getPrimaryStage() {
//        return Optional.ofNullable(primaryStage);
//    }
}
