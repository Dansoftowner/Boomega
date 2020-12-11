package com.dansoftware.libraryapp.gui.mainview;

import com.dansoftware.libraryapp.db.Database;
import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.context.ContextTransformable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class MainActivity implements ContextTransformable {

    private final BooleanProperty showing;
    private Database database;

    public MainActivity(@NotNull Database database) {
        this.database = Objects.requireNonNull(database, "The database mustn't be null");
        this.showing = new SimpleBooleanProperty();
    }

    public boolean show() {
        return false;
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    @Override
    public @NotNull Context getContext() {
        return Context.empty();
    }

    public static Optional<MainActivity> getByDatabase(DatabaseMeta databaseMeta) {
        return Optional.empty();
    }
}
