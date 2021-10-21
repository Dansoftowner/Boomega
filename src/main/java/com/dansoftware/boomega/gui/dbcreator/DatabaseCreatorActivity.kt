/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.dbcreator

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.entry.DatabaseTracker
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