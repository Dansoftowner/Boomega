package com.dansoftware.libraryapp.main;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * An abstract {@link Application} implementation.
 *
 * <p>
 * The {@link Application#init()} method must be overridden but the {@link Application#start(Stage)}
 * method doesn't.
 *
 * <p>
 * A {@link BaseApplication} should be launched through the {@link #launchApp(Class, String...)} method.
 *
 * @author Daniel Gyorffy
 */
public abstract class BaseApplication extends Application {

    @Override
    public abstract void init();

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    /**
     * Returns the application-arguments <i>(command-line params)</i> in a {@link List}.
     *
     * <p>
     * It's a basic form of {@code getParameters().getRaw()}.
     *
     * @return the list of arguments
     * @see Parameters#getRaw()
     */
    public List<String> getApplicationArgs() {
        return getParameters().getRaw();
    }

    /**
     * Launches the base-application with a {@link Preloader}.
     *
     * @param appClass the class-reference to the {@link BaseApplication} implementation
     * @param args     the application-arguments
     */
    public static void launchApp(Class<? extends BaseApplication> appClass, String... args) {
        LauncherImpl.launchApplication(appClass, Preloader.class, args);
    }
}
