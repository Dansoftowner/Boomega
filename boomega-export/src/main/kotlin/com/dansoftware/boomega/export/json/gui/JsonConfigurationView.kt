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

package com.dansoftware.boomega.export.json.gui

import com.dansoftware.boomega.export.gui.BaseConfigurationView
import com.dansoftware.boomega.export.json.JsonExportConfiguration
import com.dansoftware.boomega.gui.util.addRow
import com.dansoftware.boomega.gui.util.checkedItems
import com.dansoftware.boomega.i18n.api.i18n
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import org.controlsfx.control.CheckListView

class JsonConfigurationView(
    private val onFinished: (JsonExportConfiguration) -> Unit
) : BaseConfigurationView<JsonExportConfiguration>(JsonExportConfiguration()) {

    init {
        styleClass.addAll("json-configuration-view", "export-configuration-view")
        buildUI()
    }

    private fun buildUI() {
        addRow(Label(i18n("record.export.options")))
        addRow(JsonOptionsChecker(exportConfiguration))
        addRow(buildExecuteButton())
    }

    private fun buildExecuteButton() = Button().apply {
        setColumnSpan(this, 3)
        maxWidth = Double.MAX_VALUE
        isDefaultButton = true
        text = i18n("record.export.execute")
        setOnAction {
            onFinished(exportConfiguration)
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