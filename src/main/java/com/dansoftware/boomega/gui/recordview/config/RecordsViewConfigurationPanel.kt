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
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.collections.ListChangeListener
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import org.controlsfx.control.ListSelectionView
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.Collator
import java.util.*

class RecordsViewConfigurationPanel(private val view: RecordsView, private val preferences: Preferences) : GridPane() {

    init {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        styleClass.add("records-view-config-panel")
        padding = Insets(10.0, 20.0, 10.0, 20.0)
        VBox.setVgrow(this, Priority.ALWAYS)
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
        maxWidth = Double.MAX_VALUE
        items.addAll(I18N.getAvailableCollators().map { it.key })
        selectedItem = preferences[ABC_CONFIG_KEY]
        valueConvertingPolicy(Locale::getDisplayLanguage, Locale::forLanguageTag)
        selectedItemProperty().addListener { _, _, selected ->
            onABCSelection(
                selected,
                I18N.getABCCollator(selected).get()
            )
        }
    }

    private fun <T> buildListSelection() = ListSelectionView<T>().apply {
        setVgrow(this, Priority.ALWAYS)
        setHgrow(this, Priority.ALWAYS)

        sourceHeader = Label("Available") // TODO: i18n
        targetHeader = Label("Selected") // TODO: i18n

        fun Button.graphic(graphic: Node) = apply {
            graphicProperty().unbind()
            graphicProperty().set(graphic)
        }

        fun Button.tooltip(value: String) = apply {
            tooltipProperty().unbind()
            tooltipProperty().set(Tooltip(value))
        }

        onSkinPresent {
            // TODO: i18n
            lookup<Button>(".move-to-target-button")!!
                .graphic(icon("arrow-right-icon"))
                .tooltip("Move to target")
            lookup<Button>(".move-to-target-all-button")!!
                .graphic(icon("arrows-right-icon"))
                .tooltip("Move all")
            lookup<Button>(".move-to-source-button")!!
                .graphic(icon("arrow-left-icon"))
                .tooltip("Move to source")
            lookup<Button>(".move-to-source-all-button")!!
                .graphic(icon("arrows-left-icon"))
                .tooltip("Move back all")
        }

    }

    private fun buildDockSelection() = buildListSelection<Dock>().apply {
        // initializing
        val targetElements = preferences[DOCKS_CONFIG_KEY].docks
        val srcElements = listOf(*Dock.values()) - targetElements
        targetItems.addAll(targetElements)
        sourceItems.addAll(srcElements)
        targetItems.addListener(ListChangeListener {
            onDockSelection(targetItems.toList())
        })
    }

    private fun buildColumnSelection() = buildListSelection<BaseTable.ColumnType>().apply {
        setVgrow(this, Priority.ALWAYS)
        setHgrow(this, Priority.ALWAYS)
        val targetElements = preferences[COL_CONFIG_KEY].columnTypes
        val srcElements = RecordTable.columns() - targetElements
        targetItems.addAll(targetElements)
        sourceItems.addAll(srcElements)
        targetItems.addListener(ListChangeListener {
            onColumnSelection(targetItems.toList())
        })
    }

    private fun onColumnSelection(items: List<BaseTable.ColumnType>) {
        logger.debug("Column selection detected")
        view.columnsInfo = RecordsView.TableColumnsInfo(items)
    }

    private fun onDockSelection(items: List<Dock>) {
        logger.debug("Dock selection detected")
        view.dockInfo = RecordsView.DockInfo(items)
    }

    private fun onABCSelection(locale: Locale, collator: Collator) {
        logger.debug("ABC selection detected")
        CachedExecutor.submit { preferences.editor().put(ABC_CONFIG_KEY, locale) }
        @Suppress("UNCHECKED_CAST")
        view.sortingComparator = collator as Comparator<String>
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordsViewConfigurationPanel::class.java)
    }
}