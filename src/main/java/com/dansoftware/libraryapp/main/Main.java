package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.exception.ExceptionUtils;
import com.dansoftware.libraryapp.log.LoggerConfigurator;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class of the application
 * @author Daniel Gyorffy
 */
public class Main extends Application {

    static {
        //Configure the logger
        LoggerConfigurator loggerConfigurator = LoggerConfigurator.getInstance();
        loggerConfigurator.configureRootLogger();

        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(ExceptionUtils.getExceptionHandler());
    }

    private static Stage primaryStage;

    public static void main(String[] args) {
        new ApplicationArgumentHandler(args);

        LauncherImpl.launchApplication(Main.class, args);
    }

    @Override
    public void init() throws Exception {
        ApplicationInitializer applicationInitializer = new ApplicationInitializer();
        applicationInitializer.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }


    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
