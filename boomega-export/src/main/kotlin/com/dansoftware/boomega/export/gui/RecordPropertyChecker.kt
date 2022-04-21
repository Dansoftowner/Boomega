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

package com.dansoftware.boomega.export.gui

import com.dansoftware.boomega.database.api.data.RecordProperty
import com.dansoftware.boomega.export.api.RecordExportConfiguration
import com.dansoftware.boomega.gui.util.checkedItems
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import org.controlsfx.control.CheckListView

/**
 * List-view that allows to select the exportable record properties
 */
class RecordPropertyChecker(exportConfiguration: RecordExportConfiguration) :
    CheckListView<RecordProperty<*>>(FXCollections.observableArrayList(exportConfiguration.availableFields)) {

    init {
        GridPane.setHgrow(this, Priority.ALWAYS)
        GridPane.setVgrow(this, Priority.SOMETIMES)
        GridPane.setColumnSpan(this, 3)
        exportConfiguration.requiredFields.forEach(checkModel::check)
        checkedItems.addListener(ListChangeListener {
            exportConfiguration.requiredFields = checkedItems.toList()
        })
    }
}