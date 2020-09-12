package com.dansoftware.libraryapp.gui.dbmanager;

import com.dansoftware.libraryapp.gui.entry.DatabaseTracker;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link DatabaseManagerActivity} is used for showing a user-dialog for listing the saved databases,
 * and allows the user to watching them in the native file explorer, deleting them etc...
 *
 * @author Daniel Gyorffy
 */
public class DatabaseManagerActivity {

    public void show(@NotNull DatabaseTracker databaseTracker, @Nullable Window owner) {
        DatabaseManagerView dbManagerView = new DatabaseManagerView(databaseTracker);
        DatabaseManagerWindow window = new DatabaseManagerWindow(dbManagerView, owner);
        window.show();
    }
}
