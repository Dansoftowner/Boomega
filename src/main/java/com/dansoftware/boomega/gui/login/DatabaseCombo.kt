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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.api.checkExists
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.onScenePresent
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.gui.util.styleClass
import com.dansoftware.boomega.i18n.i18n
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class DatabaseCombo(preferences: Preferences, databaseTracker: DatabaseTracker) : ComboBox<DatabaseMeta?>() {

    init {
        HBox.setHgrow(this, Priority.ALWAYS)
        minHeight = 35.0
        minWidth = 355.0
        maxWidth = Double.MAX_VALUE
        promptText = i18n("login.source.combo.promt")
        buttonCell = ButtonCell(databaseTracker)
        setCellFactory { Cell(databaseTracker) }
        onScenePresent {
            selectedItemProperty().addListener { _, _, newItem ->
                 preferences.updateLoginData { it.selectedDatabase = newItem }
            }
        }
    }

    private open class Cell(
        private val databaseTracker: DatabaseTracker
    ) : ListCell<DatabaseMeta?>() {

        init {
            maxWidth = 650.0
        }

        override fun updateItem(item: DatabaseMeta?, empty: Boolean) {
            super.updateItem(item, empty)
            when {
                item === null || empty -> {
                    text = null
                    graphic = null
                    tooltip = null
                }
                else -> {
                    updateUi(item)
                    item.checkExists(
                        ifNotExists = ::updateUiIfNotExists,
                        ifExistsOrUnknown = {
                            if (databaseTracker.isDatabaseUsed(it))
                                updateUiIfDatabaseIsUsed()
                        }
                    )
                }
            }
        }

        private fun updateUi(database: DatabaseMeta) {
            text = database.toString()
            graphic = database.provider.icon
            tooltip = null
        }

        private fun updateUiIfNotExists(meta: DatabaseMeta) {
            tooltip = Tooltip(i18n("file.not.exists"))
            graphic = icon("warning-icon").styleClass(NOT_EXISTS_CLASS)
        }

        private fun updateUiIfDatabaseIsUsed() {
            tooltip = Tooltip(i18n("database.currently.used"))
            graphic = icon("play-icon").styleClass(USED_CLASS)
        }


        companion object {
            private const val NOT_EXISTS_CLASS = "state-indicator-file-not-exists"
            private const val USED_CLASS = "state-indicator-used"
        }

    }

    private class ButtonCell(databaseTracker: DatabaseTracker) : Cell(databaseTracker) {
        override fun updateItem(item: DatabaseMeta?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item === null) {
                text = i18n("login.source.combo.promt")
            }
        }
    }

}