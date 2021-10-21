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

package com.dansoftware.boomega.gui.export.control

import com.dansoftware.boomega.database.api.data.RecordProperty
import com.dansoftware.boomega.export.api.RecordExportConfiguration
import com.dansoftware.boomega.gui.util.selectedItem
import com.dansoftware.boomega.gui.util.selectedItemProperty
import javafx.collections.FXCollections
import javafx.scene.control.ChoiceBox

/**
 * The choice-box that allows to select the record-property to sort with
 */
class SortingPropertyChooser(
    exportConfiguration: RecordExportConfiguration
) : ChoiceBox<RecordProperty<Comparable<*>>?>(
    FXCollections.observableArrayList(listOf(null) + RecordProperty.sortableProperties)
) {

    init {
        maxWidth = Double.MAX_VALUE
        selectedItem = exportConfiguration.fieldToSortBy
        selectedItemProperty().addListener { _, _, selected ->
            exportConfiguration.fieldToSortBy = selected
        }
    }

}