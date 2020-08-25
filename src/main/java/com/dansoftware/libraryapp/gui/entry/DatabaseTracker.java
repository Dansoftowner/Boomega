package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;

/**
 * Used for tracking the opened/unopened databases (in form of {@link DatabaseMeta} objects).
 *
 * @author Daniel Gyorffy
 */
public class DatabaseTracker {

    private DatabaseTracker() {
    }

    private static final List<WeakReference<Observer>> observers =
            Collections.synchronizedList(new LinkedList<>());

    private static final ObservableSet<DatabaseMeta> savedDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<DatabaseMeta> savedDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(savedDatabases);

    private static final ObservableSet<DatabaseMeta> usingDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<DatabaseMeta> usingDatabasesUnmodifiable =
            FXCollections.unmodifiableObservableSet(usingDatabases);

    private static void iterateObservers(Consumer<Observer> observerConsumer) {
        observers.stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .forEach(observerConsumer);
    }

    public static void closingDatabase(DatabaseMeta databaseMeta) {
        usingDatabases.remove(databaseMeta);

        if (databaseMeta != null)
            iterateObservers(observer -> observer.onClosingDatabase(databaseMeta));
    }

    public static void usingDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (usingDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onUsingDatabase(databaseMeta));
        }
    }

    public static void addDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");

        if (savedDatabases.add(databaseMeta)) {
            iterateObservers(observer -> observer.onDatabaseAdded(databaseMeta));
        }
    }

    public static void removeDatabase(DatabaseMeta databaseMeta) {
        savedDatabases.remove(databaseMeta);

        if (databaseMeta != null)
            iterateObservers(observer -> observer.onDatabaseRemoved(databaseMeta));
    }

    public static void registerObserver(Observer observer) {
        if (observer != null && findWeakReference(observer).isEmpty())
            observers.add(new WeakReference<>(observer));
    }

    public static void unregisterObserver(Observer observer) {
        if (observer != null) findWeakReference(observer).ifPresent(observers::remove);
    }

    private static Optional<WeakReference<Observer>> findWeakReference(Observer observer) {
        return observers.stream()
                .filter(ref -> ref.get() == observer)
                .findAny();
    }

    public static ObservableSet<DatabaseMeta> getSavedDatabases() {
        return savedDatabasesUnmodifiable;
    }

    public static ObservableSet<DatabaseMeta> getUsingDatabases() {
        return usingDatabasesUnmodifiable;
    }

    public interface Observer {
        void onUsingDatabase(@NotNull DatabaseMeta databaseMeta);

        void onClosingDatabase(@NotNull DatabaseMeta databaseMeta);

        void onDatabaseAdded(@NotNull DatabaseMeta databaseMeta);

        void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta);
    }

}
