package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.config.ConfigurationBase;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.EntryPoint;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.main.init.ApplicationArgumentHandler;
import com.dansoftware.libraryapp.main.init.ApplicationInitializer;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.dansoftware.libraryapp.appdata.config.ConfigurationBase.getConfigurationBase;

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

    private static ApplicationArgumentHandler argumentHandler;

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
        argumentHandler = new ApplicationArgumentHandler(args);

        LauncherImpl.launchApplication(Main.class, Preloader.class, null);
    }

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
        //initializer.initializeApplication();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {

        /*
        EntryPoint entryPoint = new EntryPoint(() -> {
            if (argumentHandler.getAccount().isPresent()) {
                return argumentHandler.getAccount().get();
            } else if (getConfigurationBase().getLoggedAccount().isPresent()) {
                return getConfigurationBase().getLoggedAccount().get();
            }

            return null;
        });

        boolean close = !entryPoint.show();
        if (close) Platform.exit();

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

        runAfterStart.forEach(Runnable::run);
        runAfterStart = null;*/
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

}
