package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import com.dansoftware.libraryapp.gui.entry.Context;
import com.dansoftware.libraryapp.update.UpdateSearcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class BaseApplication extends Application {

    private InitializationResult initializationResult;

    /**
     * This method can be used for initializing the application in the background
     * during the splash screen appearing. Runs on the <i>javaFX Launcher thread</i>.
     */
    @NotNull
    protected abstract InitializationResult initialize() throws Exception;

    /**
     * This method runs after the launch of the application on the main UI-thread (<i>javaFX Application Thread<i/>).
     *
     * @param starterContext the first application-context that created.
     */
    protected abstract void postInitialize(@NotNull Context starterContext,
                                           @NotNull UpdateSearcher.UpdateSearchResult updateSearchResult) throws Exception;

    @Override
    public final void init() throws Exception {
        this.initializationResult = initialize();
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        if (Objects.isNull(initializationResult)) {
            throw new UnInitializedException(
                    "The BaseApplication must be initialized before start!",
                    new NullPointerException("The initializationResult mustn't be null!")
            );
        }

        AppEntry appEntry = new AppEntry(initializationResult.preferences);
        postInitialize(appEntry, initializationResult.updateSearchResult);
        if (BooleanUtils.isFalse(appEntry.show())) {
            Platform.exit();
        }
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
