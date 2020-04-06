package com.dansoftware.libraryapp.main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Main class of the application
 * @author Daniel Gyorffy
 */
public class Main extends Application {

    private static Stage primaryStage;

    private final String APP_VERSION = "0.0.0";
    private final String BUILD_DATE = "";

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, args);
    }

    @Override
    public void init() throws Exception {
        addSystemProperties();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.primaryStage = primaryStage;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private void addSystemProperties() {
        Properties systemProperties = System.getProperties();
        systemProperties.put("libraryapp.version", APP_VERSION);
        systemProperties.put("libraryapp.build.date", BUILD_DATE);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
