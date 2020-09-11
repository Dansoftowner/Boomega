package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DatabaseCreatorActivity {
    public Optional<DatabaseMeta> show(@NotNull DatabaseTracker databaseTracker, @Nullable Window owner) {
        DatabaseCreatorView view = new DatabaseCreatorView(databaseTracker);
        DatabaseCreatorWindow window = new DatabaseCreatorWindow(view, owner);
        window.showAndWait();

        return Optional.ofNullable(view.getCreatedDatabase());
    }
}
