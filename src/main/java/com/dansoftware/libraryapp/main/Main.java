package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.main.init.ApplicationInitializer;
import com.sun.javafx.application.LauncherImpl;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The main class and javafx application starter.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class Main extends BaseApplication {

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
    protected @NotNull Preferences initialize() {
        List<String> applicationParameters = getParameters().getRaw();
        var initializer = new ApplicationInitializer(applicationParameters);
        initializer.initializeApplication();
        return initializer.getAppConfig();
    }

    @Override
    protected void postInitialize(@NotNull AppEntry appEntry) {

    }


    @Override
    public void stop() {
    }

}
