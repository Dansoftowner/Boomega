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

package com.dansoftware.boomega.gui.record

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.control.RecordFindControl
import com.dansoftware.boomega.gui.record.dock.Dock
import com.dansoftware.boomega.gui.record.dock.DockView
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.SplitPane
import java.util.*
import java.util.stream.Collectors

class RecordsViewBase(
    private val context: Context,
    private val database: Database,
    private val baseItems: ObservableList<Record>
) : SplitPane() {

    private val dockSplitPane: SplitPane = buildDockSplitPane()

    val booksTable: RecordTable = buildBooksTable()
    val docks: ObservableList<Dock> = buildDocksList()

    private val findDialogVisible: BooleanProperty = object : SimpleBooleanProperty() {
        override fun invalidated() {
            when {
                get() -> showFindDialog()
                else -> {
                    hideFindDialog()
                    booksTable.items = baseItems
                }
            }
        }
    }

    var isFindDialogVisible: Boolean
        get() = findDialogVisible.get()
        set(value) {
            findDialogVisible.set(value)
        }

    var dockInfo: DockInfo
        get() = DockInfo(dockSplitPane)
        set(dockInfo) {
            docks.setAll(dockInfo.docks)
        }

    var columnsInfo: TableColumnsInfo
        get() = TableColumnsInfo(booksTable.showingColumns)
        set(value) {
            value.columnTypes.forEach(booksTable::addColumn)
        }

    init {
        styleClass.add("records-view")
        orientation = Orientation.VERTICAL
        buildUI()
    }

    private fun buildUI() {
        items.add(booksTable)
    }

    private fun buildBooksTable(): RecordTable =
        RecordTable(0).apply {
            items = baseItems
        }

    private fun buildDockSplitPane(): SplitPane =
        SplitPane().apply {
            orientation = Orientation.HORIZONTAL
            setResizableWithParent(this, false)
        }

    private fun buildRecordFindControl() =
        RecordFindControl(baseItems).apply {
            setOnCloseRequest { isFindDialogVisible = false }
            setOnNewResults { list -> booksTable.items = FXCollections.observableArrayList(list) }
        }

    private fun showFindDialog() {
        children.add(0, buildRecordFindControl())
    }

    private fun hideFindDialog() {
        val iterator = children.iterator()
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
                        booksTable,
                        dockSplitPane
                    )
                }
            }
            when {
                dockSplitPane.items.isEmpty() -> this@RecordsViewBase.items.remove(dockSplitPane)
                this@RecordsViewBase.items.contains(dockSplitPane).not() -> {
                    this@RecordsViewBase.items.add(dockSplitPane)
                }
            }
        }
    }

    /**
     * Used for storing the preferred table columns in the configurations.
     */
    class TableColumnsInfo(val columnTypes: List<RecordTable.ColumnType>) {
        companion object {
            fun byDefault() =
                TableColumnsInfo(
                    Arrays.stream(RecordTable.ColumnType.values())
                        .filter(RecordTable.ColumnType::isDefaultVisible)
                        .collect(Collectors.toList())
                )
        }
    }

    class DockInfo(val docks: List<Dock>) {
        constructor(dockSplitPane: SplitPane) :
                this(dockSplitPane.items.filterIsInstance<DockView<*>>().mapNotNull { Dock.parse(it.javaClass) })

        companion object {
            fun defaultInfo() =
                DockInfo(listOf(*Dock.values()))
        }
    }

    companion object {
        private const val RECORD_EDITOR_PREF_HEIGHT = 400.0
        private const val RIGHT_DOCK_PANE_PREF_WIDTH = 500.0
    }
}