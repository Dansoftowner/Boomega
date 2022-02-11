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

package com.dansoftware.boomega.database.tracking;

import com.dansoftware.boomega.config.CommonPreferences;
import com.dansoftware.boomega.config.Preferences;
import com.dansoftware.boomega.database.api.DatabaseMeta;
import com.dansoftware.boomega.gui.login.LoginDataUtilsKt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * Used for tracking the opened/unopened databases (in form of {@link DatabaseMeta} objects).
 *
 * <p>
 * Some components might use {@link DatabaseTracker}s for updating their content when something is changed
 * (a database is launched, removed etc...). This can be achieved by implementing the {@link DatabaseTracker.Observer}
 * interface.
 */
@Singleton
public class DatabaseTracker {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTracker.class);

    private final List<WeakReference<Observer>> observers =
            Collections.synchronizedList(new LinkedList<>());

    private final Set<Observer> strongObservers =
            Collections.synchronizedSet(new HashSet<>());

    private final ObservableSet<DatabaseMeta> savedDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private final ObservableSet<DatabaseMeta> savedDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(savedDatabases);

    private final ObservableSet<DatabaseMeta> usingDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private final ObservableSet<DatabaseMeta> usingDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(usingDatabases);

    @Inject
    public DatabaseTracker(@NotNull Preferences preferences) {
        preferences.get(CommonPreferences.LOGIN_DATA).getSavedDatabases().forEach(this::saveDatabase);
        initPreferencesHandling(Objects.requireNonNull(preferences));
    }

    private void initPreferencesHandling(@NotNull Preferences preferences) {
        registerObserverStrongly(new Observer() {
            @Override
            public void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) {
                LoginDataUtilsKt.updateLoginData(preferences, (loginData) -> {
                    loginData.getSavedDatabases().add(databaseMeta);
                    return Unit.INSTANCE;
                });
            }

            @Override
            public void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) {
                LoginDataUtilsKt.updateLoginData(preferences, (loginData) -> {
                    loginData.getSavedDatabases().remove(databaseMeta);
                    return Unit.INSTANCE;
                });
            }
        });
    }

    /**
     * Marks the database as closed.
     *
     * @param databaseMeta the meta database
     */
    public boolean registerClosedDatabase(@NotNull DatabaseMeta databaseMeta) {
        logger.debug("Registering database as closed: '{}'", databaseMeta.getIdentifier());
        boolean removed = usingDatabases.remove(databaseMeta);
        if (removed)
            iterateObservers(observer -> observer.onClosingDatabase(databaseMeta));
        return removed;
    }

    /**
     * Marks the database as used.
     *
     * @param databaseMeta the meta database
     */
    public boolean registerUsedDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");
        logger.debug("Registering database as used: '{}'", databaseMeta.getIdentifier());

        boolean inserted = usingDatabases.add(databaseMeta);
        if (inserted)
            iterateObservers(observer -> observer.onUsingDatabase(databaseMeta));

        return inserted;
    }

    /**
     * Registers a database.
     *
     * @param databaseMeta the meta database
     */
    public boolean saveDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");
        logger.debug("Registering database: '{}'", databaseMeta.getIdentifier());

        boolean inserted = savedDatabases.add(databaseMeta);
        if (inserted)
            iterateObservers(observer -> observer.onDatabaseAdded(databaseMeta));

        return inserted;
    }

    /**
     * Unregisters a database.
     *
     * @param databaseMeta the meta database
     */
    public boolean removeDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta);
        logger.debug("Unregistering database: '{}'", databaseMeta.getIdentifier());
        boolean removed = savedDatabases.remove(databaseMeta);
        if (removed)
            logger.debug("Removed from DatabaseTracker '{}'", databaseMeta);
        else
            logger.debug("DatabaseMeta '{}' not found in savedDatabases", databaseMeta);

        iterateObservers(observer -> observer.onDatabaseRemoved(databaseMeta));
        return removed;
    }

    /**
     * Registers an {@link Observer} that will be notified on every change.
     * <p>
     * Note: the observer will be registered as a <b>weak</b> reference, so if there is no
     * other reference that points to the observer object, the observer will be removed by the GC
     * </p>
     *
     * @param observer the observer object; shouldn't be null
     */
    public void registerObserver(@NotNull Observer observer) {
        Objects.requireNonNull(observer);
        if (findWeakReference(observer).isEmpty())
            observers.add(new WeakReference<>(observer));
    }

    public void registerObserverStrongly(@NotNull Observer observer) {
        Objects.requireNonNull(observer);
        strongObservers.add(observer);
        registerObserver(observer);
    }

    /**
     * Unregisters an {@link Observer}
     *
     * @param observer the observer object to be removed
     */
    public void unregisterObserver(@Nullable Observer observer) {
        if (observer != null) findWeakReference(observer).ifPresent(observers::remove);
    }

    public boolean hasObserver(@Nullable Observer observer) {
        return findWeakReference(observer).isPresent();
    }

    private Optional<WeakReference<Observer>> findWeakReference(Observer observer) {
        return observers.stream()
                .filter(ref -> ref.get() == observer)
                .findAny();
    }

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
