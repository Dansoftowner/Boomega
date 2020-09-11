package com.dansoftware.libraryapp.gui.dbcreator;

import com.dansoftware.libraryapp.db.DatabaseMeta;
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A {@link DatabaseCreatorActivity} is used for showing a user-dialog for creating brand new databases.
 *
 * <p>
 * It automatically updates the given {@link DatabaseTracker} when a new database is created.
 *
 * @author Daniel Gyorffy
 */
public class DatabaseCreatorActivity {

    /**
     * Shows the window and waits until it's closed.
     *
     * <p>
     * It gives the created database result in an {@link Optional}.
     *
     * @param databaseTracker the {@link DatabaseTracker} that should be used for updating the saved databases
     * @param owner           the owner window for the form's window
     * @return the wrapped result
     */
    public Optional<DatabaseMeta> show(@NotNull DatabaseTracker databaseTracker, @Nullable Window owner) {
        DatabaseCreatorView view = new DatabaseCreatorView(databaseTracker);
        DatabaseCreatorWindow window = new DatabaseCreatorWindow(view, owner);
        window.showAndWait();

        return Optional.ofNullable(view.getCreatedDatabase());
    }
}
