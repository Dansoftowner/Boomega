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
package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.byteCountToDisplaySize
import javafx.beans.binding.Bindings
import javafx.beans.binding.IntegerBinding
import javafx.collections.ObservableList
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.Callback
import org.slf4j.LoggerFactory

/**
 * A DBManagerTable is a [TableView] that is used for managing (monitoring, deleting) databases.
 *
 * @see DatabaseManagerView
 * @author Daniel Gyoerffy
 */
class DatabaseManagerTable(
    private val context: Context,
    private val databaseTracker: DatabaseTracker
) : TableView<DatabaseMeta>(), DatabaseTracker.Observer {

    private val itemsCount = Bindings.size(items)
    private val selectedItemsCount = Bindings.size(selectionModel.selectedItems)

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        databaseTracker.registerObserver(this)
        items.addAll(databaseTracker.savedDatabases)
        selectionModel.selectionMode = SelectionMode.MULTIPLE
        buildUI()
    }

    private fun buildUI() {
        placeholder = Label(i18n("database.manager.table.place.holder"))
        columns.add(StateColumn(databaseTracker))
        columns.add(TypeColumn())
        columns.add(NameColumn())
        columns.add(PathColumn())
        columns.add(SizeColumn())
        columns.add(FileOpenerColumn())
        columns.add(DeleteColumn(context, databaseTracker))
    }

    fun selectedItemsCount(): IntegerBinding {
        return selectedItemsCount
    }

    fun itemsCount(): IntegerBinding {
        return itemsCount
    }

    override fun onUsingDatabase(databaseMeta: DatabaseMeta) {
        runOnUiThread { refresh() }
    }

    override fun onClosingDatabase(databaseMeta: DatabaseMeta) {
        runOnUiThread { refresh() }
    }

    override fun onDatabaseAdded(databaseMeta: DatabaseMeta) {
        runOnUiThread { items.add(databaseMeta) }
    }

    override fun onDatabaseRemoved(databaseMeta: DatabaseMeta) {
        runOnUiThread { items.remove(databaseMeta) }
    }


    /**
     * The state-column shows an error-mark (red circle) if the particular database does not exist.
     */
    private class StateColumn(
        private val databaseTracker: DatabaseTracker
    ) : TableColumn<DatabaseMeta, String>(),
        Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        init {
            isReorderable = false
            isSortable = false
            isResizable = false
            prefWidth = 30.0
            cellFactory = this
        }

        override fun call(tableColumn: TableColumn<DatabaseMeta, String>): TableCell<DatabaseMeta, String> {
            return object : TableCell<DatabaseMeta, String>() {
                private val NOT_EXISTS_CLASS = "state-indicator-file-not-exists"
                private val USED_CLASS = "state-indicator-used"

                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    when {
                        empty -> {
                            graphic = null
                            text = null
                            tooltip = null
                        }
                        else -> {
                            val databaseMeta = tableView.items[index]!!

                            when {
                                databaseTracker.isDatabaseUsed(databaseMeta) -> {
                                    graphic = icon("play-icon").styleClass(USED_CLASS)
                                    tableRow.tooltip = Tooltip(i18n("database.currently.used"))
                                }
                                else -> {
                                    graphic = null
                                    tableRow.setTooltip(null)
                                }
                            }

                            when {
                                databaseMeta.isActionSupported(DatabaseMeta.Action.Exists) &&
                                        !databaseMeta[DatabaseMeta.Action.Exists] -> {
                                    graphic = icon("warning-icon").styleClass(NOT_EXISTS_CLASS)
                                    tooltip = Tooltip(i18n("file.not.exists"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class TypeColumn : TableColumn<DatabaseMeta, String>(i18n("database.manager.table.column.type")),
        Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {
        init {
            isReorderable = false
            cellFactory = this
        }

        override fun call(tableColumn: TableColumn<DatabaseMeta, String>): TableCell<DatabaseMeta, String> {
            return object : TableCell<DatabaseMeta, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty) {
                        graphic = null
                        text = null
                    } else {
                        val databaseMeta = tableView.items[index]
                        graphic = databaseMeta.provider.icon
                        text = databaseMeta.provider.name
                    }
                }
            }
        }
    }

    /**
     * The name-column shows the name of the database.
     */
    private class NameColumn : TableColumn<DatabaseMeta, String>(i18n("database.manager.table.column.name")) {
        init {
            isReorderable = false
            setCellValueFactory { constantObservable { it.value.name } }
        }
    }

    /**
     * The path-column shows the filepath of the database
     */
    private class PathColumn() : TableColumn<DatabaseMeta, String>(i18n("database.manager.table.column.path")) {
        init {
            isReorderable = false
            setCellValueFactory { constantObservable { it.value.uri } }
        }
    }

    /**
     * The size-column shows the file-size of the database
     */
    private class SizeColumn :
        TableColumn<DatabaseMeta, String>(i18n("database.manager.table.column.size")),
        Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        init {
            isReorderable = false
            cellFactory = this
        }

        override fun call(tableColumn: TableColumn<DatabaseMeta, String>): TableCell<DatabaseMeta, String> {
            return object : TableCell<DatabaseMeta, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    when {
                        empty -> {
                            graphic = null
                            text = null
                        }
                        else -> {
                            val databaseMeta = tableView.items[index]!!
                            text = when {
                                databaseMeta.isActionSupported(DatabaseMeta.Action.SizeInBytes) ->
                                    byteCountToDisplaySize(databaseMeta.performAction(DatabaseMeta.Action.SizeInBytes))
                                else -> "-"
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The file-opener-column provides a [Button] to open the database-file in the native
     * file-explorer.
     */
    private class FileOpenerColumn :
        TableColumn<DatabaseMeta, String>(i18n("file.open_in_explorer")),
        Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        init {
            minWidth = 90.0
            isSortable = false
            isReorderable = false
            cellFactory = this
        }

        override fun call(tableColumn: TableColumn<DatabaseMeta, String>): TableCell<DatabaseMeta, String> {
            return object : TableCell<DatabaseMeta, String>() {

                private val buttonDisableProperty get() =
                    tableRow.selectedProperty().not().or(
                        Bindings.createBooleanBinding({
                            !tableView.items[index].isActionSupported(
                                DatabaseMeta.Action.OpenInExternalApplication
                            )
                        }, selectedProperty())
                    )

                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    when {
                        empty -> {
                            text = null
                            graphic = null
                        }
                        else -> {
                            graphic = Button().apply {
                                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                                graphic = icon("folder-open-icon")
                                maxWidth = Double.MAX_VALUE
                                disableProperty().bind(buttonDisableProperty)
                                setOnAction { openDatabases() }
                            }
                        }
                    }
                }

                private fun openDatabases() {
                    tableView
                        .selectionModel
                        .selectedItems
                        .asSequence()
                        .filter { it.isActionSupported(DatabaseMeta.Action.OpenInExternalApplication) }
                        .forEach { it[DatabaseMeta.Action.OpenInExternalApplication] }
                }
            }
        }
    }

    /**
     * The delete-column provides a [Button] to delete the selected database(s).
     */
    private class DeleteColumn(private val context: Context, private val databaseTracker: DatabaseTracker) :
        TableColumn<DatabaseMeta, String>(i18n("database.manager.table.column.delete")),
        Callback<TableColumn<DatabaseMeta, String>, TableCell<DatabaseMeta, String>> {

        init {
            isReorderable = false
            isSortable = false
            minWidth = 90.0
            cellFactory = this
        }

        override fun call(tableColumn: TableColumn<DatabaseMeta, String>): TableCell<DatabaseMeta, String> {
            return object : TableCell<DatabaseMeta, String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    when {
                        empty -> {
                            text = null
                            graphic = null
                        }
                        else -> {
                            graphic = Button().apply {
                                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                                graphic = icon("database-minus-icon")
                                maxWidth = Double.MAX_VALUE
                                disableProperty().bind(tableRow.selectedProperty().not())
                                setOnAction {
                                    val dialog = DBDeleteDialog(context, databaseTracker)
                                    dialog.show(tableView.selectedItems.copy())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * A DBDeleteDialog is used for showing database-deleting dialog.
     * It's used by the [DeleteColumn].
     */
    private class DBDeleteDialog(private val context: Context, private val databaseTracker: DatabaseTracker) {
        fun show(itemsToRemove: ObservableList<DatabaseMeta>) {
            context.showDialog(
                title = i18n("database.manager.confirm_delete.title", itemsToRemove.size),
                content = ListView(itemsToRemove),
                onResult = {
                    if (it.typeEquals(ButtonType.YES)) {
                        itemsToRemove.forEach(databaseTracker::removeDatabase)
                    }
                },
                I18NButtonTypes.YES,
                I18NButtonTypes.NO
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DatabaseManagerTable::class.java)
    }
}