/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.api.i18n
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DatabaseCreatorWindow is a javaFX [Stage] that should be
 * used to display [DatabaseCreatorView] gui-objects.
 */
class DatabaseCreatorWindow(owner: Window? = null) :
    BaseWindow<DatabaseCreatorView>(
        i18n("window.dbcreator.title"),
        DatabaseCreatorView()
    ) {

    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 741.0
        height = 435.0
        centerOnScreen()
    }

    /**
     * Waits the current thread until a result is available
     *
     * @return the [DatabaseMeta] representing the registered database
     */
    fun showAndGetResult(): DatabaseMeta? {
        showAndWait()
        return content!!.createdDatabase
    }
}