package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.NotNull;
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

    public void closingDatabase(DatabaseMeta databaseMeta) {
        usingDatabases.remove(databaseMeta);

        if (databaseMeta != null)
            iterateObservers(observer -> observer.onClosingDatabase(databaseMeta));
    }

    public void usingDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (usingDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onUsingDatabase(databaseMeta));
        }
    }

    public void addDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (savedDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onDatabaseAdded(databaseMeta));
        }
    }

    public void removeDatabase(DatabaseMeta databaseMeta) {
        if (savedDatabases.remove(databaseMeta))
            logger.debug("Removed from DatabaseTracker '{}'", databaseMeta);
        else
            logger.debug("DatabaseMeta '{}' not found in savedDatabases", databaseMeta);

        if (databaseMeta != null)
            iterateObservers(observer -> observer.onDatabaseRemoved(databaseMeta));
    }

    public void registerObserver(Observer observer) {
        if (observer != null && findWeakReference(observer).isEmpty())
            observers.add(new WeakReference<>(observer));
    }

    public void unregisterObserver(Observer observer) {
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
        default void onUsingDatabase(@NotNull DatabaseMeta databaseMeta) { }

        default void onClosingDatabase(@NotNull DatabaseMeta databaseMeta) { }

        default void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta) { }

        default void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta) { }
    }
}
