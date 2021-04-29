package com.dansoftware.boomega.gui.record.show

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.control.RecordFindControl
import com.dansoftware.boomega.gui.record.show.dock.Dock
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import java.util.*
import java.util.stream.Collectors

class RecordsViewBase(
    private val context: Context,
    private val database: Database,
    private val baseItems: ObservableList<Record>
) : SplitPane() {

    val booksTable: RecordTable = buildBooksTable()
    val docks: ObservableList<Dock> = buildDocksList()

    private val horizontalSplitPane: SplitPane = buildHorizontalSplitPane()

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
        get() = DockInfo(docks)
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
        items.add(horizontalSplitPane)
    }

    private fun buildBooksTable(): RecordTable =
        RecordTable(0).apply {
            items = baseItems
        }

    private fun buildHorizontalSplitPane(): SplitPane =
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
                change.removed.forEach { it.removeFrom(horizontalSplitPane) }
                change.addedSubList.forEach {
                    it.align(
                        context,
                        database,
                        booksTable,
                        horizontalSplitPane
                    )
                }
            }
            when {
                horizontalSplitPane.items.isEmpty() -> this@RecordsViewBase.items.remove(horizontalSplitPane)
                this@RecordsViewBase.items.contains(horizontalSplitPane).not() -> {
                    this@RecordsViewBase.items.add(horizontalSplitPane)
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