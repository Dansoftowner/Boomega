package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import com.dansoftware.libraryapp.gui.entry.login.data.LoginData;
import com.dansoftware.libraryapp.gui.launcher.ActivityLauncher;
import com.dansoftware.libraryapp.gui.launcher.LauncherMode;
import com.dansoftware.libraryapp.gui.updateview.UpdateActivity;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(BaseApplication.class);

    /**
     * This method can be used for initializing the application in the background
     * during the splash screen appearing. Runs on the <i>javaFX Launcher thread</i>.
     */
    @NotNull
    protected abstract InitializationResult initialize() throws Exception;

    @Override
    public final void init() throws Exception {
        InitializationResult initRes = initialize();

        Preferences preferences = initRes.preferences;
        LoginData loginData = preferences.get(Preferences.Key.LOGIN_DATA);
        UpdateSearcher.UpdateSearchResult updateSearchResult = initRes.updateSearchResult;

        new ActivityLauncher(LauncherMode.INIT) {
            @Override
            protected LoginData getLoginData() {
                return loginData;
            }

            @Override
            protected void saveLoginData(LoginData loginData) {
                loginData.getLastDatabases().forEach(DatabaseTracker::addDatabase);

                preferences.editor()
                        .set(Preferences.Key.LOGIN_DATA, loginData)
                        .tryCommit();
            }

            @Override
            protected void onActivityLaunched(Context context) {
                UpdateActivity updateActivity = new UpdateActivity(context, updateSearchResult);
                updateActivity.show(false);
            }
        }.launch();
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
    }

    public static void launch(String... args) {
        LauncherImpl.launchApplication(Main.class, Preloader.class, args);
    }

    static class InitializationResult {
        private final Preferences preferences;
        private final UpdateSearcher.UpdateSearchResult updateSearchResult;

        InitializationResult(@NotNull Preferences preferences, @NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) {
            this.preferences = preferences;
            this.updateSearchResult = updateSearchResult;
        }
    }

    public static class UnInitializedException extends RuntimeException {
        private UnInitializedException() {
        }

        private UnInitializedException(String message) {
            super(message);
        }

        public UnInitializedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
