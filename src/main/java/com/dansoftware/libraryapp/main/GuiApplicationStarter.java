package com.dansoftware.libraryapp.main;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * The javafx application starter.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class GuiApplicationStarter extends Application {

    /**
     * Contains the primary window of the Window Hierarchy
     * Should be initialized by the start() method
     *
     * @see GuiApplicationStarter#start(Stage)
     */
    private static Stage primaryStage;

    /**
     * This list contains all the tasks that are should be executed after
     * the application finally starts
     *
     * @see GuiApplicationStarter#start
     */
    private static List<Runnable> runAfterStart = new LinkedList<>();

    @Override
    public void init() {

        try {
            ApplicationInitializer initializer = new ApplicationInitializer();
            initializer.initializeApplication();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage primaryStage) {
        GuiApplicationStarter.primaryStage = primaryStage;

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
     * @see GuiApplicationStarter#start
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
