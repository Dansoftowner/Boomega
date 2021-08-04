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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.control.BaseTable
import com.dansoftware.boomega.gui.recordview.RecordTable
import com.dansoftware.boomega.gui.recordview.RecordsView
import com.dansoftware.boomega.gui.recordview.RecordsView.Companion.ABC_CONFIG_KEY
import com.dansoftware.boomega.gui.recordview.RecordsView.Companion.COL_CONFIG_KEY
import com.dansoftware.boomega.gui.recordview.RecordsView.Companion.DOCKS_CONFIG_KEY
import com.dansoftware.boomega.gui.recordview.RecordsViewBase
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.util.selectedItemProperty
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.invoke
import javafx.geometry.Insets
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import jfxtras.styles.jmetro.JMetroStyleClass
import org.controlsfx.control.ListSelectionView
import java.text.Collator
import java.util.*
import java.util.function.Supplier

class RecordsViewConfigurationPanel(private val view: RecordsView, private val preferences: Preferences) : GridPane() {

    init {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        padding = Insets(5.0)
        buildUI()
    }

    private fun buildUI() {
        // TODO: i18n
        addRow(0, Label("ABC:"), buildABCChooserMenu())
        addRow(1, Label("Dock:"), buildDockSelection())
        addRow(2, Label("Column:"), buildColumnSelection())
    }

    private fun buildABCChooserMenu() = ChoiceBox<Locale>().apply {
        setHgrow(this, Priority.ALWAYS)
        I18N.getAvailableCollators().forEach { locale, getCollator ->
            items.add(locale)
            selectedItemProperty().addListener { _, _, selectedItem ->
                if (selectedItem == locale) {
                    onABCSelection(locale, getCollator)
                }
            }
        }
    }

    private fun buildDockSelection() = ListSelectionView<Dock>().apply {
        setVgrow(this, Priority.ALWAYS)
        setHgrow(this, Priority.ALWAYS)
        val targetElements = preferences[DOCKS_CONFIG_KEY].docks
        val srcElements = listOf(*Dock.values()) - targetElements
        targetItems.addAll(targetElements)
        sourceItems.addAll(srcElements)
        targetItemsProperty().addListener { _, _, items ->
            onDockSelection(items)
        }
    }

    private fun buildColumnSelection() = ListSelectionView<BaseTable.ColumnType>().apply {
        setVgrow(this, Priority.ALWAYS)
        setHgrow(this, Priority.ALWAYS)
        val targetElements = preferences[COL_CONFIG_KEY].columnTypes
        val srcElements = RecordTable.columns() - targetElements
        targetItems.addAll(targetElements)
        sourceItems.addAll(srcElements)
        targetItemsProperty().addListener { _, _, items ->
            onColumnSelection(items)
        }
    }

    private fun onColumnSelection(items: List<BaseTable.ColumnType>) {
        preferences.editor().put(COL_CONFIG_KEY, RecordsViewBase.TableColumnsInfo(items))
    }

    private fun onDockSelection(items: List<Dock>) {
        preferences.editor().put(DOCKS_CONFIG_KEY, RecordsViewBase.DockInfo(items))
    }

    private fun onABCSelection(locale: Locale, getCollator: Supplier<Collator>) {
        preferences.editor().put(ABC_CONFIG_KEY, locale)
    }
}