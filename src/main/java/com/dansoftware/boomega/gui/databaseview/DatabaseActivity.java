/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.databaseview;

import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.db.Database;
import com.dansoftware.boomega.db.DatabaseMeta;
import com.dansoftware.boomega.gui.api.Context;
import com.dansoftware.boomega.gui.entry.DatabaseTracker;
import com.dansoftware.boomega.gui.menubar.AppMenuBar;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;

public class DatabaseActivity {

    private static final Set<WeakReference<DatabaseActivity>> instances = Collections.synchronizedSet(new HashSet<>());

    private final BooleanProperty showing;
    private final Preferences preferences;
    private final DatabaseTracker databaseTracker;
    private final DatabaseView databaseView;
    private Database database;

    public DatabaseActivity(@NotNull Database database, @NotNull Preferences preferences, @NotNull DatabaseTracker databaseTracker) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
        this.preferences = Objects.requireNonNull(preferences);
        this.databaseTracker = Objects.requireNonNull(databaseTracker);
        this.showing = new SimpleBooleanProperty();
        this.databaseView = new DatabaseView(preferences, database);
        instances.add(new WeakReference<>(this));
        databaseTracker.usingDatabase(database.getMeta());
    }

    public boolean show() {
        final DatabaseWindow databaseWindow = new DatabaseWindow(
                databaseView,
                new AppMenuBar(
                        databaseView,
                        preferences,
                        databaseTracker
                )
        );
        databaseWindow.show();
        databaseWindow.addEventHandler(
                WindowEvent.WINDOW_HIDDEN,
                event -> {
                    database.close();
                    databaseTracker.closingDatabase(database.getMeta());
                    database = null;
                });
        return true;
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    public Context getContext() {
        return databaseView;
    }

    public static Optional<DatabaseActivity> getByDatabase(DatabaseMeta databaseMeta) {
        return instances.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(activity -> activity.database != null && activity.database.getMeta().equals(databaseMeta))
                .findAny();
    }
}
