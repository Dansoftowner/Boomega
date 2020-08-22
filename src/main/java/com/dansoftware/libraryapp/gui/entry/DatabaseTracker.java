package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Used for tracking the opened/unopened databases (in form of {@link DatabaseMeta} objects).
 *
 * @author Daniel Gyorffy
 */
public class DatabaseTracker {

    private DatabaseTracker() {
    }

    private static final List<Observer> observers =
            Collections.synchronizedList(new LinkedList<>());

    private static final ObservableSet<DatabaseMeta> savedDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    private static final ObservableSet<DatabaseMeta> usingDatabases =
            FXCollections.synchronizedObservableSet(FXCollections.observableSet());

    public static void closingDatabase(DatabaseMeta databaseMeta) {
        usingDatabases.remove(databaseMeta);

        if (databaseMeta != null)
            for (Observer observer : observers)
                observer.onClosingDatabase(databaseMeta);
    }

    public static void usingDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");
        if (savedDatabases.contains(databaseMeta))
            return;

        usingDatabases.add(databaseMeta);
        for (Observer observer : observers)
            observer.onUsingDatabase(databaseMeta);
    }

    public static void addDatabase(@NotNull DatabaseMeta databaseMeta) {
        Objects.requireNonNull(databaseMeta, "The DatabaseMeta shouldn't be null");
        if (savedDatabases.contains(databaseMeta))
            return;

        savedDatabases.add(databaseMeta);
        for (Observer observer : observers)
            observer.onDatabaseRegistered(databaseMeta);
    }

    public static void removeDatabase(DatabaseMeta databaseMeta) {
        savedDatabases.remove(databaseMeta);

        if (databaseMeta != null)
            for (Observer observer : observers)
                observer.onDatabaseRemoved(databaseMeta);
    }

    public static void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public static void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public interface Observer {
        void onUsingDatabase(@NotNull DatabaseMeta databaseMeta);

        void onClosingDatabase(@NotNull DatabaseMeta databaseMeta);

        void onDatabaseRegistered(@NotNull DatabaseMeta databaseMeta);

        void onDatabaseRemoved(@NotNull DatabaseMeta databaseMeta);
    }

}
