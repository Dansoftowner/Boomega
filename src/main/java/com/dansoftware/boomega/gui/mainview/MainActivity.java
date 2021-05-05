package com.dansoftware.boomega.gui.mainview;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.context.Context;
import com.dansoftware.boomega.gui.context.ContextTransformable;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.menubar.AppMenuBar;
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
        this.mainView = new MainView(preferences, database);
        instances.add(new WeakReference<>(this));
        databaseTracker.usingDatabase(database.getMeta());
    }

    public boolean show() {
        final MainWindow mainWindow = new MainWindow(
                mainView,
                new AppMenuBar(
                        mainView.getContext(),
                        mainView,
                        preferences,
                        databaseTracker
                )
        );
        mainWindow.show();
        mainWindow.addEventHandler(
                WindowEvent.WINDOW_HIDDEN,
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
