package com.dansoftware.libraryapp.main;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.gui.entry.AppEntry;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

public abstract class BaseApplication extends Application {

    private Preferences preferences;

    @NotNull
    protected abstract Preferences initialize();

    protected abstract void postInitialize(@NotNull AppEntry appEntry);

    @Override
    public final void init() throws Exception {
        this.preferences = initialize();
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        AppEntry appEntry = new AppEntry(preferences);
        if (BooleanUtils.isFalse(appEntry.show())) {
            Platform.exit();
        }

        postInitialize(appEntry);
    }
}
