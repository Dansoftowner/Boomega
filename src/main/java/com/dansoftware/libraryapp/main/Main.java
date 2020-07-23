package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.entry.login.LoginData;
import com.dansoftware.libraryapp.gui.theme.Theme;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

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
        LOGGER.info("Configurations has been read successfully!");

        AppArgumentHandler argumentHandler = new AppArgumentHandler(getParameters().getRaw());
        argumentHandler.getDB().ifPresent(databaseMeta -> {
            LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
            loginData.getLastDatabases().add(databaseMeta);
            loginData.selectLastDatabase();

            preferences.editor().set(Preferences.Key.LOGIN_DATA, loginData).tryCommit();
        });

        Locale.setDefault(preferences.get(Preferences.Key.LOCALE));
        LOGGER.debug("Locale is: {}", Locale.getDefault());

        Theme.setDefault(preferences.get(Preferences.Key.THEME));
        LOGGER.debug("Theme is: {}", Theme.getDefault());


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
