package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.updateview.UpdateActivity;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        InstanceService.open(args);

        LauncherImpl.launchApplication(Main.class, Preloader.class, args);
    }

    @NotNull
    @Override
    protected BaseApplication.InitializationResult initialize() throws Exception {
        Preferences preferences = Preferences.getPreferences();
        logger.info("Configurations has been read successfully!");

        //we check the application arguments
        AppArgumentHandler argumentHandler = new AppArgumentHandler(getParameters().getRaw());
        //we add the launched database to the preferences
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

        //searching for updates
        UpdateSearcher updateSearcher = new UpdateSearcher(Globals.VERSION_INFO);
        UpdateSearcher.UpdateSearchResult searchResult = updateSearcher.search();

        return new InitializationResult(preferences, searchResult);
    }

    @Override
    protected void postInitialize(@NotNull Context starterContext,
                                  @NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) {
        //showing an updateActivity for the user
        Platform.runLater(() -> {
            UpdateActivity updateActivity = new UpdateActivity(starterContext, updateSearchResult);
            updateActivity.show(false);
        });
    }


    @Override
    public void stop() throws IOException {
        //writing all configurations
        Preferences preferences = Preferences.getPreferences();
        preferences.editor().commit();
    }

}
