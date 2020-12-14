package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.appdata.Preferences;
import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class MainActivity implements ContextTransformable {

    private final BooleanProperty showing;
    private final Preferences preferences;
    private final DatabaseTracker databaseTracker;
    private Database database;

    private MainView mainView;


    public MainActivity(@NotNull Database database, @NotNull Preferences preferences, @NotNull DatabaseTracker databaseTracker) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
        this.preferences = Objects.requireNonNull(preferences);
        this.databaseTracker = Objects.requireNonNull(databaseTracker);
        this.showing = new SimpleBooleanProperty();
    }

    public boolean show() {
        this.mainView = new MainView(database, preferences, databaseTracker);
        new MainWindow(mainView).show();
        return true;
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public @NotNull Context getContext() {
        return mainView.getContext();
    }

    public static Optional<MainActivity> getByDatabase(DatabaseMeta databaseMeta) {
        return Optional.empty();
    }
}
