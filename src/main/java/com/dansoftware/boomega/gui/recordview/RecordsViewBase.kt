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

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.BaseTable
import com.dansoftware.boomega.gui.control.RecordFindControl
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.recordview.dock.DockView
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
import java.util.stream.Collectors

class RecordsViewBase(
    private val context: Context,
    private val database: Database,
    private val baseItems: ObservableList<Record>
) : SplitPane() {

    private val dockSplitPane: SplitPane = buildDockSplitPane()

    val table: RecordTable = buildBooksTable()
    val docks: ObservableList<Dock> = buildDocksList()

    private val findDialogVisible: BooleanProperty = object : SimpleBooleanProperty() {
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
        get() = TableColumnsInfo(table.showingColumnTypes)
        set(value) {
            value.columnTypes.forEach(table::addColumnType)
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
            items = baseItems
            VBox.setVgrow(this, Priority.ALWAYS)
        }

    private fun buildDockSplitPane(): SplitPane =
        SplitPane().apply {
            orientation = Orientation.HORIZONTAL
            setResizableWithParent(this, false)
        }

    private fun buildRecordFindControl() =
        RecordFindControl(baseItems).apply {
            setOnCloseRequest { isFindDialogVisible = false }
            setOnNewResults { list -> table.items = FXCollections.observableArrayList(list) }
        }

    private fun showFindDialog() {
        logger.debug("Showing find dialog...")
        (table.parent as VBox).children.add(0, buildRecordFindControl())
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
    class TableColumnsInfo(val columnTypes: List<BaseTable.ColumnType>) {
        companion object {
            fun byDefault() =
                TableColumnsInfo(
                    RecordTable.columns().stream()
                        .filter(BaseTable.ColumnType::isDefaultVisible)
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
        private val logger: Logger = LoggerFactory.getLogger(RecordsViewBase::class.java)
    }
}