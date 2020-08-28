package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.gui.launcher.ActivityLauncher;
import com.dansoftware.libraryapp.gui.launcher.LauncherMode;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.updateview.UpdateActivity;
import com.dansoftware.libraryapp.log.LogFile;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.sun.javafx.application.LauncherImpl;
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
        BaseApplication.launchApp(Main.class, args);
    }

    @Override
    public void init() {
        Preferences preferences = Preferences.getPreferences();
        logger.info("Configurations has been read successfully!");

        //adding the saved databases from the login-data to DatabaseTracker
        LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
        loginData.getLastDatabases().forEach(DatabaseTracker::addDatabase);

        //setting the default locale
        Locale.setDefault(preferences.get(Preferences.Key.LOCALE));
        logger.debug("Locale is: {}", Locale.getDefault());

        //setting the default theme
        Theme.setDefault(preferences.get(Preferences.Key.THEME));
        logger.debug("Theme is: {}", Theme.getDefault());

        //searching for updates
        UpdateSearcher updateSearcher = new UpdateSearcher(Globals.VERSION_INFO);
        UpdateSearcher.UpdateSearchResult searchResult = updateSearcher.search();


        new ActivityLauncher(LauncherMode.INIT, getParameters().getRaw()) {
            @Override
            protected LoginData getLoginData() {
                return loginData;
            }

            @Override
            protected void saveLoginData(LoginData loginData) {
                preferences.editor()
                        .set(Preferences.Key.LOGIN_DATA, loginData)
                        .tryCommit();
            }

            @Override
            protected void onNewDatabaseAdded(DatabaseMeta databaseMeta) {
                DatabaseTracker.addDatabase(databaseMeta);
            }

            @Override
            protected void onActivityLaunched(Context context) {
                UpdateActivity updateActivity = new UpdateActivity(context, searchResult);
                updateActivity.show(false);
            }
        }.launch();
    }


    @Override
    public void stop() throws IOException {
        //writing all configurations
        Preferences preferences = Preferences.getPreferences();
        preferences.editor().commit();
    }
}
