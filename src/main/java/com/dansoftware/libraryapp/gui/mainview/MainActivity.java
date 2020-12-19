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
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;

public class MainActivity implements ContextTransformable {

    private static final Set<WeakReference<MainActivity>> instances = Collections.synchronizedSet(new HashSet<>());

    private final BooleanProperty showing;
    private final Preferences preferences;
    private final DatabaseTracker databaseTracker;
    private final MainView mainView;
    private Database database;

    public MainActivity(@NotNull Database database, @NotNull Preferences preferences, @NotNull DatabaseTracker databaseTracker) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
        this.preferences = Objects.requireNonNull(preferences);
        this.databaseTracker = Objects.requireNonNull(databaseTracker);
        this.showing = new SimpleBooleanProperty();
        this.mainView = new MainView(this, database, preferences, databaseTracker);
        instances.add(new WeakReference<>(this));
        databaseTracker.usingDatabase(database.getMeta());
    }

    public boolean show() {
        final MainWindow mainWindow = new MainWindow(mainView);
        mainWindow.show();
        mainWindow.addEventHandler(
                WindowEvent.WINDOW_CLOSE_REQUEST,
                event -> {
                    database.close();
                    databaseTracker.closingDatabase(database.getMeta());
                });
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
        return instances.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(activity -> activity.database.getMeta().equals(databaseMeta))
                .findAny();
    }
}
