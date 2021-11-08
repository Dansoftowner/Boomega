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

package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.database.tracking.DatabaseTracker
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