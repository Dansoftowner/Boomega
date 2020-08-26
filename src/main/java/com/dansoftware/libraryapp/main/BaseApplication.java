package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
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

import java.util.Objects;

public abstract class BaseApplication extends Application {

    /*private volatile Preferences preferences;
    private volatile LoginData loginData;
    private volatile UpdateSearcher.UpdateSearchResult updateSearchResult;*/

   /* protected void setPreferences(@NotNull Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "Preferences shouldn't be null");
        this.loginData = preferences.get(Preferences.Key.LOGIN_DATA);
    }

    protected void setUpdateSearchResult(@NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) {
        this.updateSearchResult = updateSearchResult;
    }*/

    /*
     * This method can be used for initializing the application in the background
     * during the splash screen appearing. Runs on the <i>javaFX Launcher thread</i>.
     * <p>
     * It should set the default {@link Preferences} object with the {@link #setPreferences(Preferences)} method,
     * and the {@link com.dansoftware.libraryapp.update.UpdateSearcher.UpdateSearchResult} with the
     * {@link #setUpdateSearchResult(UpdateSearcher.UpdateSearchResult)} method.
    protected abstract void initialize() throws Exception;*/

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
