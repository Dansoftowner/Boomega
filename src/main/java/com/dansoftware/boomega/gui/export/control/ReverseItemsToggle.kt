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
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip

class ReverseItemsToggle(exportConfiguration: RecordExportConfiguration) : ToggleButton() {
    init {
        tooltip = Tooltip(i18n("record.export.reverse_order"))
        graphic = icon("rotate-icon")
        selectedProperty().addListener { _, _, isSelected ->
            exportConfiguration.reverseItems = isSelected
        }
    }
}