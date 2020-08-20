package com.dansoftware.libraryapp.gui.entry;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.reflections.Reflections;

/**
 * Used for tracking the opened/unopened databases (in form of {@link DatabaseMeta} objects).
 *
 * @author Daniel Gyorffy
 */
public class DatabaseTracker {

    private DatabaseTracker() {
    }

    private static final ObservableList<DatabaseMeta> openedDatabases;

    private static final ObservableList<DatabaseMeta> unOpenedDatabases;

    static {
        openedDatabases = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
        unOpenedDatabases = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        openedDatabases.addListener((ListChangeListener<? super DatabaseMeta>) change -> {
            if (change.wasAdded()) {
                unOpenedDatabases.remove(change.getFrom(), change.getTo());
            }
        });

        unOpenedDatabases.addListener((ListChangeListener<? super DatabaseMeta>) change -> {
            if (change.wasAdded()) {
                openedDatabases.remove(change.getFrom(), change.getTo());
            }
        });
    }

    public static ObservableList<DatabaseMeta> getOpenedDatabases() {
        return openedDatabases;
    }

    public static ObservableList<DatabaseMeta> getUnOpenedDatabases() {
        return unOpenedDatabases;
    }

}
