package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.gui.entry.DatabaseTracker
import javafx.stage.Window

/**
 * A [DatabaseManagerActivity] is used for showing a user-dialog for listing the saved databases,
 * and allows the user to watching them in the native file explorer, deleting them etc...
 *
 * @author Daniel Gyorffy
 */
class DatabaseManagerActivity {
    fun show(databaseTracker: DatabaseTracker, owner: Window?) {
        val dbManagerView = DatabaseManagerView(databaseTracker)
        val window = DatabaseManagerWindow(dbManagerView, owner)
        window.show()
    }
}