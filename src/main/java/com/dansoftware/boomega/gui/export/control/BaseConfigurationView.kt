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
import com.dansoftware.boomega.gui.util.addRow
import com.dansoftware.boomega.i18n.i18n
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass

open class BaseConfigurationView<C : RecordExportConfiguration>(
    protected val exportConfiguration: C
) : GridPane() {

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.addAll(JMetroStyleClass.BACKGROUND)
        padding = Insets(20.0)
        hgap = 10.0
        vgap = 10.0
        buildUI()
    }

    private fun buildUI() {
        initColumnConstraints()
        addRow(
            Label(i18n("record.export.sort_by")),
            Label(i18n("record.export.sorting_abc"))
        )
        addRow(
            SortingPropertyChooser(exportConfiguration),
            SortingAbcChooser(exportConfiguration),
            ReverseItemsToggle(exportConfiguration)
        )
        addRow(Label(i18n("record.export.fields")))
        addRow(RecordPropertyChecker(exportConfiguration))
    }

    private fun initColumnConstraints() {
        columnConstraints.addAll(
            List(3) {
                ColumnConstraints().apply {
                    hgrow = Priority.ALWAYS
                }
            }
        )
    }
}