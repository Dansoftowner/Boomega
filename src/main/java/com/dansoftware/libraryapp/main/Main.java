package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.main.init.AppArgumentHandler;
import com.sun.javafx.application.LauncherImpl;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * The main class and javafx application starter.
 *
 * <p>
 * Responsible for initializing the application and launching the GUI
 *
 * @author Daniel Gyorffy
 */
public class Main extends BaseApplication {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static {
        //Configure the logger
        var logFile = new LogFile();
        System.setProperty("log.file.path", logFile.getAbsolutePath());
        System.setProperty("log.file.path.full", logFile.getPathWithExtension());

        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

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

    @NotNull
    @Override
    protected Preferences initialize() {

        Preferences preferences = Preferences.getPreferences();
        logger.info("Configurations has been read successfully!");

        //we check the application arguments
        AppArgumentHandler argumentHandler = new AppArgumentHandler(getParameters().getRaw());
        argumentHandler.getDB().ifPresent(databaseMeta -> {
            preferences.editor().modify(Preferences.Key.LOGIN_DATA, loginData -> {
                loginData.getLastDatabases().add(databaseMeta);
                loginData.selectLastDatabase();
            }).tryCommit();
        });

        //setting the default locale
        Locale.setDefault(preferences.get(Preferences.Key.LOCALE));
        logger.debug("Locale is: {}", Locale.getDefault());

        //setting the default theme
        Theme.setDefault(preferences.get(Preferences.Key.THEME));
        logger.debug("Theme is: {}", Theme.getDefault());


        /*List<String> applicationParameters = getParameters().getRaw();
        var initializer = new ApplicationInitializer(applicationParameters);
        initializer.initializeApplication();
        return initializer.getAppConfig();*/

        return preferences;
    }

    @Override
    protected void postInitialize(@NotNull AppEntry appEntry) {

    }


    @Override
    public void stop() {
    }

}
