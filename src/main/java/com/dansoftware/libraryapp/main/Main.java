package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.appdata.logindata.LoginData;
import com.dansoftware.libraryapp.exception.UncaughtExceptionHandler;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.firsttime.FirstTimeActivity;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.updateview.UpdateActivity;
import com.dansoftware.libraryapp.launcher.ActivityLauncher;
import com.dansoftware.libraryapp.launcher.LauncherMode;
import com.dansoftware.libraryapp.locale.I18N;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.dansoftware.libraryapp.util.ReflectionUtils;
import com.dansoftware.libraryapp.util.adapter.VersionInteger;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
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

    private static final Logger logger;

    private static final Object initThreadLock;

    static {
        //object for synchronizing the JavaFX Launcher Thread
        initThreadLock = Main.class;

        PropertiesResponsible.setupSystemProperties();
        //we create the logger after the necessary system-properties are put
        logger = LoggerFactory.getLogger(Main.class);
        //Set the default uncaught exception handler
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    }

    /**
     * The main-method of the application;
     *
     * <p>
     * Starts the {@link InstanceService}.
     * <p>
     * If the {@link InstanceService} didn't stop the app,
     * the main launches the application with a
     * preloader.
     *
     * @see BaseApplication#launchApp(Class, String...)
     * @see InstanceService#open(String[])
     */
    public static void main(String[] args) {
        InstanceService.open(args);
        BaseApplication.launchApp(Main.class, args);
    }

    @Override
    public void init() throws Exception {
        synchronized (initThreadLock) {
            //if a file is passed as a parameter, we show a message about it on the Preloader
            getFormattedArgument(ArgumentTransformer::transform).ifPresent(file ->
                    notifyPreloader(new Preloader.FixedMessageNotification("preloader.file.open", file.getName())));

            notifyPreloader(new Preloader.MessageNotification("preloader.preferences.read"));
            Preferences preferences = Preferences.getPreferences();
            logger.info("Configurations has been read successfully!");

            logger.debug("Entering synchronized block with FirstTimeDialog.threadLock");

            //creating and showing a FirstTimeDialog
            if (FirstTimeActivity.isNeeded()) {
                notifyPreloader(new Preloader.HideNotification());
                logger.debug("FirstTimeDialog needed");
                Platform.runLater(() -> {
                    synchronized (initThreadLock) {
                        FirstTimeActivity firstTimeDialog = new FirstTimeActivity(preferences);
                        firstTimeDialog.show();
                        initThreadLock.notify();
                    }
                });
                //we wait until the FirstTimeDialog completes
                initThreadLock.wait();
                notifyPreloader(new Preloader.ShowNotification());
            } else {
                notifyPreloader(new Preloader.MessageNotification("preloader.lang"));
                Locale.setDefault(preferences.get(Preferences.Key.LOCALE));

                notifyPreloader(new Preloader.MessageNotification("preloader.theme"));
                Theme.setDefault(preferences.get(Preferences.Key.THEME));
            }

            logger.debug("Theme is: {}", Theme.getDefault());
            logger.debug("Locale is: {}", Locale.getDefault());

            //adding the saved databases from the login-data to DatabaseTracker
            notifyPreloader(new Preloader.MessageNotification("preloader.logindata"));
            DatabaseTracker databaseTracker = DatabaseTracker.getGlobal();
            LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
            loginData.getSavedDatabases().forEach(databaseTracker::addDatabase);

            //searching for updates
            notifyPreloader(new Preloader.MessageNotification("preloader.update.search"));
            UpdateSearcher updateSearcher = new UpdateSearcher(new VersionInteger(System.getProperty("libraryapp.version")));
            UpdateSearcher.UpdateSearchResult searchResult = updateSearcher.search();

            notifyPreloader(new Preloader.MessageNotification("preloader.gui.build"));
            new InitActivityLauncher(
                    getApplicationArgs(),
                    preferences,
                    databaseTracker,
                    loginData,
                    searchResult
            ).launch();
        }
    }


    @Override
    public void stop() throws IOException {
        //writing all configurations
        Preferences preferences = Preferences.getPreferences();
        preferences.editor().commit();

        //we make sure the app exiting, even if there are non-daemon running threads in the background
        System.exit(0);
    }

    /**
     * An {@link ActivityLauncher} implementation for starting the application.
     *
     * <p>
     * When an activity is launched, it also shows an "notifier box" if
     * a new update is available.
     */
    private static final class InitActivityLauncher extends ActivityLauncher {

        private final Preferences preferences;
        private final LoginData loginData;
        private final UpdateSearcher.UpdateSearchResult searchResult;

        private InitActivityLauncher(@NotNull List<String> args,
                                     @NotNull Preferences preferences,
                                     @NotNull DatabaseTracker databaseTracker,
                                     @NotNull LoginData loginData,
                                     @NotNull UpdateSearcher.UpdateSearchResult searchResult) {
            super(LauncherMode.INIT, preferences, databaseTracker, args);
            this.preferences = preferences;
            this.loginData = loginData;
            this.searchResult = searchResult;
        }

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
        protected void onActivityLaunched(Context context) {
            UpdateActivity updateActivity = new UpdateActivity(context, searchResult);
            updateActivity.show(false);
        }
    }
}
