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
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.i18n
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DBManagerWindow is used for displaying a [DatabaseManagerView] in a window.
 */
class DatabaseManagerWindow(databaseTracker: DatabaseTracker, owner: Window?) :
    BaseWindow<DatabaseManagerView>(i18n("window.dbmanager.title"), DatabaseManagerView(databaseTracker)) {

    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 1000.0
        height = 430.0
        centerOnScreen()
    }
}