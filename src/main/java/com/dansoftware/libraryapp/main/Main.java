package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.init.ApplicationInitializer;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Main class of the application
 * @author Daniel Gyorffy
 */
public class Main extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
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
