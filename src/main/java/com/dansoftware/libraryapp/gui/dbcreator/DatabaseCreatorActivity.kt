package com.dansoftware.libraryapp.gui.dbcreator

import com.dansoftware.libraryapp.db.DatabaseMeta
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker
import javafx.stage.Window
import java.util.*

/**
 * A [DatabaseCreatorActivity] is used for showing a user-dialog for creating brand new databases.
 *
 *
 *
 * It automatically updates the given [DatabaseTracker] when a new database is created.
 *
 * @author Daniel Gyorffy
 */
class DatabaseCreatorActivity {
    /**
     * Shows the window and waits until it's closed.
     *
     *
     *
     * It gives the created database result in an [Optional].
     *
     * @param databaseTracker the [DatabaseTracker] that should be used for updating the saved databases
     * @param owner           the owner window for the form's window
     * @return the wrapped result
     */
    fun show(databaseTracker: DatabaseTracker, owner: Window?): Optional<DatabaseMeta> {
        val view = DatabaseCreatorView(databaseTracker)
        val window = DatabaseCreatorWindow(view, owner)
        window.showAndWait()
        return Optional.ofNullable(view.createdDatabase)
    }
}