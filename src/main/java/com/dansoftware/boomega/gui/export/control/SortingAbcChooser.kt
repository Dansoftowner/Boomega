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

import com.dansoftware.boomega.export.api.RecordExportConfiguration
import com.dansoftware.boomega.gui.util.onValuePresent
import com.dansoftware.boomega.gui.util.selectedItem
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.gui.util.valueConvertingPolicy
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.control.ChoiceBox
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import java.util.*

/**
 * The choice-box that allows to select the abc to sort with
 */
class SortingAbcChooser(exportConfiguration: RecordExportConfiguration) : ChoiceBox<Locale>() {
    init {
        GridPane.setHgrow(this, Priority.ALWAYS)
        maxWidth = Double.MAX_VALUE
        items.addAll(listOf(Locale.forLanguageTag("")) + I18N.getAvailableCollators().map { it.key })
        selectedItem = exportConfiguration.sortingAbc
        valueConvertingPolicy(Locale::getDisplayLanguage, Locale::forLanguageTag)
        selectedItemProperty().onValuePresent {
            exportConfiguration.sortingAbc = it
        }
    }
}