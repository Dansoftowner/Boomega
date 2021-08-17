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

package com.dansoftware.boomega.gui.export.json

import com.dansoftware.boomega.export.json.JsonExportConfiguration
import com.dansoftware.boomega.gui.export.control.RecordPropertyChecker
import com.dansoftware.boomega.gui.export.control.SortingAbcChooser
import com.dansoftware.boomega.gui.export.control.SortingPropertyChooser
import com.dansoftware.boomega.gui.util.addRow
import com.dansoftware.boomega.gui.util.checkedItems
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ToggleButton
import javafx.scene.control.Tooltip
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import org.controlsfx.control.CheckListView

class JsonConfigurationView(
    private val onFinished: (JsonExportConfiguration) -> Unit
) : GridPane() {

    private val jsonExportConfiguration = JsonExportConfiguration()

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add(JMetroStyleClass.BACKGROUND)
        styleClass.add("json-configuration-view")
        padding = Insets(20.0)
        hgap = 10.0
        vgap = 10.0
        buildUI()
    }

    private fun buildUI() {
        initColumnConstraints()
        addRow(Label(i18n("record.export.sort_by")), Label(i18n("record.export.sorting_abc")))
        addRow(SortingPropertyChooser(jsonExportConfiguration), SortingAbcChooser(jsonExportConfiguration), buildReverseItemsToggle())
        addRow(Label(i18n("record.export.options")))
        addRow(JsonOptionsChecker(jsonExportConfiguration))
        addRow(Label(i18n("record.export.fields")))
        addRow(RecordPropertyChecker(jsonExportConfiguration))
        addRow(buildExecuteButton())
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

    private fun buildReverseItemsToggle() =
        ToggleButton().apply {
            tooltip = Tooltip(i18n("record.export.reverse_order"))
            graphic = icon("rotate-icon")
            selectedProperty().addListener { _, _, isSelected ->
                jsonExportConfiguration.reverseItems = isSelected
            }
        }

    private fun buildExecuteButton() = Button().apply {
        setColumnSpan(this, 3)
        maxWidth = Double.MAX_VALUE
        isDefaultButton = true
        text = i18n("record.export.execute")
        setOnAction {
            onFinished(jsonExportConfiguration)
        }
    }

    /**
     * The list-view that allows to select some options regarding the json-export.
     */
    private class JsonOptionsChecker(
        private val jsonExportConfiguration: JsonExportConfiguration
    ) : CheckListView<JsonOptionsChecker.Entry>(
        FXCollections.observableArrayList(
            Entry(
                i18n("record.export.json.pretty_printing"),
                checkedInitially = jsonExportConfiguration.prettyPrinting
            ) { jsonExportConfiguration.prettyPrinting = it },
            Entry(
                i18n("record.export.json.non_executable"),
                checkedInitially = jsonExportConfiguration.nonExecutableJson
            ) { jsonExportConfiguration.nonExecutableJson = it },
            Entry(
                i18n("record.export.json.serialize_nulls"),
                checkedInitially = jsonExportConfiguration.serializeNulls
            ) { jsonExportConfiguration.serializeNulls = it }
        )
    ) {

        init {
            setHgrow(this, Priority.ALWAYS)
            setVgrow(this, Priority.SOMETIMES)
            setColumnSpan(this, 3)
            items.filter { it.checkedInitially }.forEach(checkModel::check)
            checkedItems.addListener(ListChangeListener {
                val uncheckedItems = items - checkedItems
                checkedItems.forEach { it(true) }
                uncheckedItems.forEach { it(false) }
            })
        }

        private class Entry(
            private val text: String,
            val checkedInitially: Boolean,
            action: (Boolean) -> Unit
        ) : (Boolean) -> Unit by action {
            override fun toString() = text
        }
    }
}