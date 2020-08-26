package com.dansoftware.libraryapp.main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

public abstract class BaseApplication extends Application {

    @Override
    public abstract void init();

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    /**
     * Launches the base-application with a {@link Preloader}.
     *
     * @param appClass the class-reference to the {@link BaseApplication} implementation
     * @param args the application-arguments
     */
    public static void launchApp(Class<? extends BaseApplication> appClass, String... args) {
        LauncherImpl.launchApplication(appClass, Preloader.class, args);
    }
}
