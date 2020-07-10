package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.config.AppConfig;
import com.dansoftware.libraryapp.appdata.config.LoginData;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.main.init.ApplicationArgumentHandler;
import com.dansoftware.libraryapp.main.init.ApplicationInitializer;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;

import java.io.Closeable;
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

        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    private static AppConfig appConfig;

    /**
     * The main-method of the application;
     *
     * <p>
     * Launches the application with a
     * preloader.
     *
     * @see LauncherImpl#launchApplication(Class, Class, String[])
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, Preloader.class, args);
    }

    @Override
    public void init() {
        List<String> applicationParameters = getParameters().getRaw();
        var initializer = new ApplicationInitializer(applicationParameters);
        initializer.initializeApplication();

        appConfig = initializer.getAppConfig();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        LoginData loginData = appConfig.get(AppConfig.Key.LOGIN_DATA);

        AppEntry appEntry = new AppEntry(loginData);
        if (BooleanUtils.isFalse(appEntry.show())) {
            Platform.exit();
        }
    }

    @Override
    public void stop() {
    }

    public static AppConfig getAppConfig() {
        return appConfig;
    }

}
