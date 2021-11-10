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

import com.dansoftware.boomega.database.SupportedDatabases
import com.dansoftware.boomega.database.api.DatabaseProvider
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.selectedItem
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.i18n.I18N
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.Property
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.ToolBar

class DatabaseCreatorToolbar(private val databaseType: ObjectProperty<DatabaseProvider<*>>) : BiToolBar() {

    init {
        styleClass.add("database-creator-toolbar")
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(icon("database-plus-icon"))
        leftItems.add(buildLabel())
        rightItems.add(DatabaseTypeChooser(databaseType))
    }

    private fun buildLabel() =
        Label(I18N.getValue("database.creator.title"))

    private class DatabaseTypeChooser(databaseType: ObjectProperty<DatabaseProvider<*>>) :
        ComboBox<DatabaseProvider<*>>() {

        private val listCell
            get() = object : ListCell<DatabaseProvider<*>>() {
                override fun updateItem(item: DatabaseProvider<*>?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        graphic = null
                        text = null
                    } else {
                        graphic = item.icon
                        text = item.name
                    }
                }
            }

        init {
            setCellFactory { listCell }
            buttonCell = listCell
            databaseType.bind(selectedItemProperty())
            items.addAll(SupportedDatabases)
            selectedItem = items[0]

            // For preventing the user to select nothing
            selectedItemProperty().addListener { _, oldItem, newItem ->
                if (newItem == null) selectionModel.select(oldItem)
            }
        }

    }
}