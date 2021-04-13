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
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import java.util.*
import java.util.stream.Collectors

class RecordsViewBase(
    private val context: Context,
    private val database: Database,
    private val baseItems: ObservableList<Record>
) : VBox() {

    val booksTable: RecordTable = buildBooksTable()
    val docks: ObservableList<Dock> = buildDocksList()

    private val baseSplitPane: SplitPane = buildBaseSplitPane()
    private val leftSplitPane: SplitPane = buildLeftSplitPane()
    private val rightSplitPane: SplitPane = buildRightSplitPane()

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
        get() = DockInfo(docks, recordTablePos)
        set(dockInfo) {
            docks.setAll(dockInfo.docks)
            recordTablePos = dockInfo.recordTablePos
        }

    var columnsInfo: TableColumnsInfo
        get() = TableColumnsInfo(booksTable.showingColumns)
        set(value) {
            value.columnTypes.forEach(booksTable::addColumn)
        }

    private var recordTablePos: Int
        get() = leftSplitPane.items.indexOf(booksTable)
        set(pos) {
            leftSplitPane.items.add(pos, booksTable)
        }

    init {
        buildUI()
    }

    private fun buildUI() {
        baseSplitPane.items.add(leftSplitPane)
        children.add(baseSplitPane)
    }

    private fun buildBooksTable(): RecordTable =
        RecordTable(0).apply {
            items = baseItems
        }

    private fun buildBaseSplitPane(): SplitPane =
        SplitPane().apply {
            styleClass.add("records-view")
            orientation = Orientation.HORIZONTAL
            setVgrow(this, Priority.ALWAYS)
        }

    private fun buildLeftSplitPane(): SplitPane =
        buildDockSplitPane().apply {
            SplitPane.setResizableWithParent(this, true)
        }

    private fun buildRightSplitPane(): SplitPane =
        buildDockSplitPane().apply {
            prefWidth = 500.0
            maxWidth = 500.0
            SplitPane.setResizableWithParent(this, false)
        }

    private fun buildDockSplitPane(): SplitPane =
        SplitPane().apply {
            orientation = Orientation.VERTICAL
        }

    private fun buildRecordFindControl() =
        RecordFindControl(baseItems).apply {
            setOnCloseRequest { isFindDialogVisible = false }
            setOnNewResults { list -> booksTable.items = FXCollections.observableArrayList(list) }
        }

    fun setDockFullyResizable() {
        rightSplitPane.maxWidth = USE_COMPUTED_SIZE
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
                change.removed.forEach { it.remove(leftSplitPane, rightSplitPane) }
                change.addedSubList.forEach {
                    it.align(
                        context,
                        database,
                        booksTable,
                        leftSplitPane,
                        rightSplitPane
                    )
                }
            }
            when {
                rightSplitPane.items.isEmpty() -> baseSplitPane.items.remove(rightSplitPane)
                baseSplitPane.items.contains(rightSplitPane).not() -> {
                    baseSplitPane.items.add(rightSplitPane)
                    rightSplitPane.prefWidth = RIGHT_DOCK_PANE_PREF_WIDTH
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

    class DockInfo(val docks: List<Dock>, val recordTablePos: Int) {
        companion object {
            fun defaultInfo() =
                DockInfo(listOf(*Dock.values()), 0)
        }
    }

    companion object {
        private const val RECORD_EDITOR_PREF_HEIGHT = 400.0
        private const val RIGHT_DOCK_PANE_PREF_WIDTH = 500.0
    }
}