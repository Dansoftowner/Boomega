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

package com.dansoftware.boomega.gui.recordview

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.RecordFindControl
import com.dansoftware.boomega.gui.recordview.RecordsView.Companion.COL_CONFIG_KEY
import com.dansoftware.boomega.gui.recordview.RecordsView.Companion.DOCKS_CONFIG_KEY
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.recordview.dock.DockView
import com.dansoftware.boomega.gui.util.selectedItems
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class RecordsViewBase(
    private val context: Context,
    private val preferences: Preferences,
    private val database: Database,
    private val baseItems: ObservableList<Record>
) : SplitPane() {

    val table: RecordTable = buildBooksTable()
    private val docks: ObservableList<Dock> = buildDocksList()
    private val dockSplitPane: SplitPane = buildDockSplitPane()

    @get:JvmName("findDialogVisibleProperty")
    val findDialogVisibleProperty: BooleanProperty = object : SimpleBooleanProperty() {
        override fun invalidated() {
            when {
                get() -> showFindDialog()
                else -> {
                    hideFindDialog()
                    table.items = baseItems
                }
            }
        }
    }

    private val dockSplitPaneVisibleProperty = object : SimpleBooleanProperty() {
        override fun invalidated() {
            alignDockSplitPane(get())
        }

        fun update() {
            set(isDockSplitPaneNeeded())
        }
    }

    var isFindDialogVisible: Boolean
        get() = findDialogVisibleProperty.get()
        set(value) {
            findDialogVisibleProperty.set(value)
        }

    var docksList: List<Dock>
        get() = dockSplitPane.items.filterIsInstance<DockView<*>>().mapNotNull(DockView<*>::getDockEnum)
        set(value) {
            docks.setAll(value)
        }

    init {
        styleClass.add("records-view")
        orientation = Orientation.VERTICAL
        buildUI()
    }

    private fun buildUI() {
        items.add(VBox(table))
    }

    private fun buildBooksTable(): RecordTable =
        RecordTable(0).apply {
            VBox.setVgrow(this, Priority.ALWAYS)
            items = baseItems
            selectedItems.addListener(ListChangeListener { dockSplitPaneVisibleProperty.update() })
            columns.addListener(ListChangeListener { updateTableColumnsConfiguration() })
        }

    private fun buildDockSplitPane(): SplitPane =
        SplitPane().apply {
            setResizableWithParent(this, false)
            orientation = Orientation.HORIZONTAL
            items.addListener(ListChangeListener { updateDocksConfiguration() })
        }

    private fun buildRecordFindControl() =
        RecordFindControl(baseItems).apply {
            onCloseRequest = fun() { isFindDialogVisible = false }
            onNewResults = fun(items) { table.items = FXCollections.observableArrayList(items) }
        }

    private fun showFindDialog() {
        logger.debug("Showing find dialog...")
        val recordFindControl = buildRecordFindControl()
        (table.parent as VBox).children.add(0, recordFindControl)
        recordFindControl.requestFocus()
    }

    private fun hideFindDialog() {
        logger.debug("Hiding find dialog...")
        val iterator = (table.parent as VBox).children.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element is RecordFindControl) {
                element.releaseListeners()
                iterator.remove()
                break
            }
        }
    }

    private fun buildDocksList(): ObservableList<Dock> = FXCollections.observableArrayList<Dock>().apply {
        addListener { change: ListChangeListener.Change<out Dock> ->
            while (change.next()) {
                change.removed.forEach { it.removeFrom(dockSplitPane) }
                change.addedSubList.forEach {
                    it.align(
                        context,
                        database,
                        table,
                        dockSplitPane
                    )
                }
            }
           dockSplitPaneVisibleProperty.update()
        }
    }

    private fun alignDockSplitPane(isDockSplitPaneNeeded: Boolean) {
        if (isDockSplitPaneNeeded.and(dockSplitPane !in items)) items.add(dockSplitPane)
        else items.remove(dockSplitPane)
    }

    private fun isDockSplitPaneNeeded(): Boolean =
        table.selectedItems.isEmpty().not().and(docks.isEmpty().not())

    /**
     * Used for updating the docks configuration in case the order is changed.
     */
    private fun updateDocksConfiguration() {
        val docks = docksList
        CachedExecutor.submit {
            preferences.editor()[DOCKS_CONFIG_KEY] = RecordsView.DockInfo(docks)
        }
    }

    /**
     * Used for updating the table columns configuration in case the order is changed.
     */
    private fun updateTableColumnsConfiguration() {
        val columns = table.showingColumnTypes
        CachedExecutor.submit {
            preferences.editor()[COL_CONFIG_KEY] = RecordsView.TableColumnsInfo(columns)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordsViewBase::class.java)
    }
}