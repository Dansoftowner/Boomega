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

package com.dansoftware.boomega.gui.entry;

import com.dansoftware.boomega.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * Used for tracking the opened/unopened databases (in form of {@link DatabaseMeta} objects).
 *
 * <p>
 * Other objects are using {@link DatabaseTracker}s for updating their content when something is changed
 * (a database is launched, removed etc...). This can be achieved by implementing the {@link DatabaseTracker.Observer}
 * interface.
 *
 * <p>
 * {@link DatabaseTracker} is not singleton, but it has a global instance that can be
 * used through the static {@link #getGlobal()} method.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseTracker {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTracker.class);

    private static DatabaseTracker globalInstance;

    private final List<WeakReference<Observer>> observers =
            Collections.synchronizedList(new LinkedList<>());

    private final ObservableSet<DatabaseMeta> savedDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private final ObservableSet<DatabaseMeta> savedDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(savedDatabases);

    private final ObservableSet<DatabaseMeta> usingDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private final ObservableSet<DatabaseMeta> usingDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(usingDatabases);

    private void iterateObservers(Consumer<Observer> observerConsumer) {
        Consumer<Observer> safeConsumer = observer -> {
            try {
                observerConsumer.accept(observer);
            } catch (Exception e) {
                logger.error("exception caught from observer", e);
            }
        };

        observers.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .forEach(safeConsumer);
    }

    /**
     * @deprecated use {@link #registerClosedDatabase(DatabaseMeta)} instead
     */
    @Deprecated
    public void closingDatabase(@NotNull DatabaseMeta databaseMeta) {
        registerClosedDatabase(databaseMeta);
    }

    /**
     * Marks the database as closed.
     *
     * @param databaseMeta the meta database
     */
    public void registerClosedDatabase(@NotNull DatabaseMeta databaseMeta) {
        usingDatabases.remove(databaseMeta);
        iterateObservers(observer -> observer.onClosingDatabase(databaseMeta));
    }

    /**
     * @deprecated use {@link #registerUsedDatabase(DatabaseMeta)} instead
     */
    public void usingDatabase(@NotNull DatabaseMeta databaseMeta) {
        registerUsedDatabase(databaseMeta);
    }

    /**
     * Marks the database as used.
     *
     * @param databaseMeta the meta database
     */
    public void registerUsedDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (usingDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onUsingDatabase(databaseMeta));
        }
    }

    /**
     * @deprecated use {@link #saveDatabase(DatabaseMeta)} instead
     */
    @Deprecated
    public void addDatabase(@NotNull DatabaseMeta databaseMeta) {
        saveDatabase(databaseMeta);
    }

    /**
     * Registers a database.
     *
     * @param databaseMeta the meta database
     */
    public void saveDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (savedDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onDatabaseAdded(databaseMeta));
        }
    }

    /**
     * Unregisters a database.
     *
     * @param databaseMeta the meta database
     */
    public void removeDatabase(@Nullable DatabaseMeta databaseMeta) {
        if (savedDatabases.remove(databaseMeta))
            logger.debug("Removed from DatabaseTracker '{}'", databaseMeta);
        else
            logger.debug("DatabaseMeta '{}' not found in savedDatabases", databaseMeta);

        if (databaseMeta != null)
            iterateObservers(observer -> observer.onDatabaseRemoved(databaseMeta));
    }

    /**
     * Registers an {@link Observer} that will be notified on every change.
     * <p>
     *     Note: the observer will be registered as a <b>weak</b> reference, so if there is no
     *     other reference that points to the observer object, the observer will be removed by the GC
     * </p>
     *
     * @param observer the observer object; shouldn't be null
     */
    public void registerObserver(@NotNull Observer observer) {
        Objects.requireNonNull(observer);
        if (findWeakReference(observer).isEmpty())
            observers.add(new WeakReference<>(observer));
    }

    /**
     * Unregisters an {@link Observer}
     *
     * @param observer the observer object to be removed
     */
    public void unregisterObserver(@Nullable Observer observer) {
        if (observer != null) findWeakReference(observer).ifPresent(observers::remove);
    }

    private Optional<WeakReference<Observer>> findWeakReference(Observer observer) {
        return observers.stream()
                .filter(ref -> ref.get() == observer)
                .findAny();
    }

    public boolean isDatabaseSaved(DatabaseMeta databaseMeta) {
        return savedDatabases.contains(databaseMeta);
    }

    public boolean isDatabaseNotSaved(DatabaseMeta databaseMeta) {
        return !isDatabaseSaved(databaseMeta);
    }

    public boolean isDatabaseClosed(DatabaseMeta databaseMeta) {
        return !isDatabaseUsed(databaseMeta);
    }

    public boolean isDatabaseUsed(DatabaseMeta databaseMeta) {
        return usingDatabases.contains(databaseMeta);
    }

    public ObservableSet<DatabaseMeta> getSavedDatabases() {
        return savedDatabasesUnmodifiable;
    }

    public ObservableSet<DatabaseMeta> getUsingDatabases() {
        return usingDatabasesUnmodifiable;
    }

    public static DatabaseTracker getGlobal() {
        return globalInstance == null ? (globalInstance = new DatabaseTracker()) : globalInstance;
    }

    public interface Observer {
        default void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) {
        }

        default void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) {
        }

        default void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
        }

        default void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
        }
    }
}
