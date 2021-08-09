/*
 * Boomega
 * Copyright (C)  $originalComment.match("Copyright (\d+)", 1, "-")2021  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.recordview.config

import com.dansoftware.boomega.gui.control.BaseTable
import com.dansoftware.boomega.gui.recordview.RecordTable
import com.dansoftware.boomega.gui.recordview.RecordsView
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import org.controlsfx.control.CheckListView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Panel that allows the user to configure some properties of a [RecordsView].
 *
 * These properties are:
 * - ABC of sorting ([RecordsView.sortingAbc])
 * - Docks ([RecordsView.dockInfo])
 * - Columns ([RecordsView.columnsInfo])
 */
class RecordsViewConfigurationPanel(private val view: RecordsView) : GridPane() {

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.add(JMetroStyleClass.BACKGROUND)
        styleClass.add("records-view-config-panel")
        padding = Insets(10.0, 20.0, 10.0, 20.0)
        hgap = 10.0
        vgap = 10.0
        buildUI()
    }

    private fun buildUI() {
        add(Label(i18n("record.table.abc")), 0, 0)
        add(buildABCChooserMenu(), 0, 1)
        add(Label(i18n("record.preferred_docks")), 0, 2)
        add(buildDockSelection(), 0, 3)
        add(Label(i18n("record.table_preferred_columns")), 0, 4)
        add(buildColumnSelection(), 0, 5)
    }

    private fun buildABCChooserMenu() = ChoiceBox<Locale>().apply {
        setHgrow(this, Priority.ALWAYS)
        maxWidth = Double.MAX_VALUE
        items.addAll(I18N.getAvailableCollators().map { it.key })
        selectedItem = view.sortingAbc
        valueConvertingPolicy(Locale::getDisplayLanguage, Locale::forLanguageTag)
        selectedItemProperty().onValuePresent(::onABCSelection)
    }

    private fun buildDockSelection() =
        CheckListView(FXCollections.observableArrayList(*Dock.values())).apply {
            // initializing
            val checked = view.dockInfo.docks
            checked.forEach(checkModel::check)

            checkedItems.addListener(ListChangeListener { onDockSelection(checkedItems.toList()) })
        }

    private fun buildColumnSelection() =
        CheckListView(FXCollections.observableArrayList(*RecordTable.columns().toTypedArray())).run {
            // initializing
            val checked = view.columnsInfo.columnTypes
            checked.forEach(checkModel::check)

            checkedItems.addListener(ListChangeListener { onColumnSelection(checkedItems.toList()) })

            VBox(3.0, this, buildColumnResetButton())
        }

    private fun CheckListView<BaseTable.ColumnType>.buildColumnResetButton() = Button().apply {
        maxWidth = Double.MAX_VALUE
        text = i18n("record.table.colreset")
        graphic = icon("columns-icon")
        setOnAction {
            val defaults = RecordTable.columns().filter { it.isDefaultVisible }
            val toUncheck = checkedItems - defaults
            val toCheck = defaults - checkedItems
            toUncheck.forEach(checkModel::clearCheck)
            toCheck.forEach(checkModel::check)
            onColumnSelection(defaults)
        }
    }

    private fun onColumnSelection(items: List<BaseTable.ColumnType>) {
        logger.debug("Column selection detected")
        view.columnsInfo = RecordsView.TableColumnsInfo(items)
    }

    private fun onDockSelection(items: List<Dock>) {
        logger.debug("Dock selection detected")
        view.dockInfo = RecordsView.DockInfo(items)
    }

    private fun onABCSelection(locale: Locale) {
        logger.debug("ABC selection detected")
        view.sortingAbc = locale
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordsViewConfigurationPanel::class.java)
    }
}