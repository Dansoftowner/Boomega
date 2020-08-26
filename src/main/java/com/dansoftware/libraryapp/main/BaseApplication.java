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
