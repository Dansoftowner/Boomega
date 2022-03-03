/*
 * Boomega
 * Copyright (c) 2020-2022  Daniel Gyoerffy
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

import com.dansoftware.boomega.config.ConfigAdapter
import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.export.api.RecordExportConfiguration
import com.dansoftware.boomega.export.api.RecordExporter
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.clipboard.RecordClipboard
import com.dansoftware.boomega.gui.control.BaseTable
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.open
import com.dansoftware.boomega.util.revealInExplorer
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.IntegerBinding
import javafx.beans.property.BooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.Event
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.text.Collator
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.stream.Collectors

class RecordsView(
    private val context: Context,
    private val database: Database,
    private val preferences: Preferences
) : BorderPane() {

    private val copyHandle = Any()
    private val baseItems: ObservableList<Record> = FXCollections.observableArrayList()

    private val recordsViewBase = RecordsViewBase(context, preferences, database, baseItems)
    private val toolbar = RecordsViewToolbar(context, this, preferences)

    val table: RecordTable
        get() = recordsViewBase.table

    val findDialogVisibleProperty: BooleanProperty
        get() = recordsViewBase.findDialogVisibleProperty

    var isFindDialogVisible: Boolean
        get() = recordsViewBase.isFindDialogVisible
        set(value) {
            recordsViewBase.isFindDialogVisible = value
        }

    var dockInfo: DockInfo
        get() = DockInfo(recordsViewBase.docksList)
        set(value) {
            recordsViewBase.docksList = value.docks
        }

    var sortingAbc: Locale = I18N.defaultLocale()
        set(value) {
            field = value
            configureSortingAbc(value)
        }

    var columnsInfo: TableColumnsInfo
        get() = TableColumnsInfo(table.showingColumnTypes)
        set(value) {
            table.columns.clear()
            value.columnTypes.forEach(table::addColumnType)
        }

    init {
        buildUI()
        init()
    }

    private fun buildUI() {
        top = toolbar
        center = recordsViewBase
    }

    private fun init() {
        initKeyDetections()
        buildTableRowContextMenu()
        readConfigurations()
        loadRecords()
    }

    private fun initKeyDetections() {
        addKeyBindingDetection(KeyBindings.findRecord) { isFindDialogVisible = true }
    }

    private fun buildTableRowContextMenu() {
        RecordContextMenu(this).applyOn(table)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readConfigurations() {
        columnsInfo = preferences.get(COL_CONFIG_KEY)
        sortingAbc = preferences.get(ABC_CONFIG_KEY)
        dockInfo = preferences.get(DOCKS_CONFIG_KEY)
    }

    private fun configureSortingAbc(locale: Locale) {
        @Suppress("UNCHECKED_CAST")
        table.sortingComparator = I18N.getABCCollator(locale).orElse(Collator.getInstance()) as Comparator<String>
        get(ExecutorService::class, "cachedExecutor").submit {
            preferences.editor()[ABC_CONFIG_KEY] = locale
        }
    }

    @JvmOverloads
    fun refresh(onSucceeded: () -> Unit = {}) {
        loadRecords(onSucceeded)
    }

    private fun loadRecords(onSucceeded: () -> Unit = {}) {
        get(ExecutorService::class, "cachedExecutor").submit(buildRecordsLoadTask(onSucceeded))
    }

    private fun buildRecordsLoadTask(onSucceeded: () -> Unit) =
        TableRecordsGetTask(
            context,
            table,
            database
        ).apply {
            addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                baseItems.setAll(this.value)
                onSucceeded()
            }
        }

    fun clipboardEmptyProperty(): BooleanBinding = RecordClipboard.emptyProperty()
        .or(RecordClipboard.identifierProperty().isEqualTo(copyHandle))

    fun selectedItemsCount(): IntegerBinding =
        Bindings.size(table.selectionModel.selectedItems)

    fun itemsCountProperty(): IntegerBinding =
        Bindings.size(baseItems)

    fun cutSelectedToClipboard() {
        cutItemsToClipboard(table.selectionModel.selectedItems.toList())
    }

    private fun cutItemsToClipboard(items: List<Record>) {
        RecordClipboard.pushItems(copyHandle, RecordClipboard.Action.CUT, items).also { push ->
            push.onPulled { removeItems(it.items) }
        }
    }

    fun copySelectedToClipboard() {
        copyItemsToClipboard(table.selectionModel.selectedItems.toList())
    }

    private fun copyItemsToClipboard(items: List<Record>) {
        RecordClipboard.pushItems(copyHandle, RecordClipboard.Action.COPY, items)
    }

    fun pasteItemsFromClipboard() {
        get(ExecutorService::class, "cachedExecutor").submit(buildPasteAction(RecordClipboard.pullContent().items))
    }

    private fun buildPasteAction(items: List<Record>) =
        object : Task<List<Record>>() {
            init {
                setOnFailed { e ->
                    //TODO: error dialog
                    context.stopProgress()
                    logger.error("Couldn't paste record elements", e.source.exception)
                }
                setOnRunning { context.showIndeterminateProgress() }
                setOnSucceeded {
                    context.stopProgress()
                    refresh {
                        value.takeIf { it.isNotEmpty() }
                            ?.also { table.selectionModel.clearSelection() } // clearing previous selections
                            ?.onEach { Platform.runLater { table.selectionModel.select(it) } }
                            ?.let { table.scrollTo(it[0]) }
                    }
                }
            }

            override fun call(): List<Record> {
                synchronized(RecordClipboard) {
                    logger.debug("Performing paste action....")
                    return items.stream()
                        .map(Record::copy)
                        .peek { it.id = null }
                        .collect(Collectors.toList())
                        .onEach(database::insertRecord)
                }
            }
        }

    fun removeSelectedItems() {
        //TODO: showing confirmation dialog
        removeItems(ArrayList(table.selectionModel.selectedItems))
    }

    fun insertNewRecord(record: Record = Record(Record.Type.BOOK)) {
        get(ExecutorService::class, "cachedExecutor").submit(buildInsertAction(record))
    }

    private fun buildInsertAction(record: Record): Task<Unit> =
        object : Task<Unit>() {

            init {
                setOnRunning {
                    context.showIndeterminateProgress()
                }

                setOnFailed {
                    context.stopProgress()
                    context.showErrorDialog(
                        I18N.getValue("record.add.error.title"),
                        I18N.getValue("record.add.error.msg"),
                        it.source.exception as Exception?
                    )
                }

                setOnSucceeded {
                    context.stopProgress()
                    baseItems.add(record)
                    table.selectionModel.clearSelection()
                    table.selectionModel.select(record)
                    table.scrollTo(record)
                }
            }

            override fun call() {
                database.insertRecord(record)
            }
        }

    private fun removeItems(items: List<Record>) {
        get(ExecutorService::class, "cachedExecutor").submit(buildRemoveAction(items))
    }

    private fun buildRemoveAction(items: List<Record>): Task<Unit> =
        object : Task<Unit>() {
            init {
                setOnRunning { context.showIndeterminateProgress() }
                setOnSucceeded {
                    context.stopProgress()
                    baseItems.removeAll(items)
                }
                setOnFailed { context.stopProgress() }
            }

            override fun call() {
                synchronized(RecordClipboard) {
                    logger.debug("Performing remove action...")
                    database.removeRecords(items)
                }
            }
        }

    fun duplicateSelectedItems() {
        get(ExecutorService::class, "cachedExecutor").submit(buildPasteAction(table.selectedItems.toList()))
    }

    /**
     * Exports the selected table-items with the given [RecordExporter]
     */
    fun <C : RecordExportConfiguration> exportSelected(exporter: RecordExporter<C>) {
        export(exporter, table.selectedItems.map(Record::copy))
    }

    private fun <C : RecordExportConfiguration> export(exporter: RecordExporter<C>, items: List<Record>) {
        exporter.configurationDialog.show(context) { config ->
            val fileExplorer = FileChooser()
            fileExplorer.extensionFilters.add(
                FileChooser.ExtensionFilter(
                    exporter.contentTypeDescription,
                    "*.${exporter.contentType}"
                )
            )
            fileExplorer.showSaveDialog(context.contextWindow)?.let { file ->
                val task = exporter.task(items, FileOutputStream(file), config).apply {
                    onSucceeded {
                        context.stopProgress()
                        context.showInformationNotification(
                            i18n("record.export.successful.title"),
                            i18n("record.export.successful.msg", items.size, exporter.contentType, file.name),
                            Event::consume,
                            hyperLink(i18n("file.open_in_app")) { file.open() },
                            hyperLink(i18n("file.open_in_explorer")) { file.revealInExplorer() }
                        )
                    }
                    onFailed { e ->
                        context.stopProgress()
                        logger.error("Couldn't export records to '{}'", exporter.contentType, e)
                        context.showErrorDialog(
                            i18n("record.export.error.title"),
                            i18n("record.export.error.msg", items.size, exporter.contentType),
                            e as? Exception
                        ) { }
                    }
                    onRunning { context.showIndeterminateProgress() }
                }
                get(ExecutorService::class, "cachedExecutor").submit(task)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RecordsView::class.java)

        val COL_CONFIG_KEY =
            PreferenceKey(
                "books.view.table.columns",
                TableColumnsInfo::class.java,
                ColumnsInfoAdapter(),
                TableColumnsInfo.Companion::byDefault
            )

        val DOCKS_CONFIG_KEY =
            PreferenceKey(
                "books.view.dock.info",
                DockInfo::class.java,
                DockInfo.Companion::defaultInfo
            )

        val ABC_CONFIG_KEY =
            PreferenceKey(
                "books.view.module.table.abcsort",
                Locale::class.java,
                Locale::getDefault
            )
    }

    private class ColumnsInfoAdapter : ConfigAdapter<TableColumnsInfo> {
        override fun serialize(
            src: TableColumnsInfo,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement =
            JsonArray().also { jsonArray ->
                src.columnTypes
                    .map { it.id }
                    .distinct()
                    .forEach(jsonArray::add)
            }

        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): TableColumnsInfo =
            TableColumnsInfo(
                json.asJsonArray
                    .mapNotNull { RecordTable.columnById(it.asString).orElse(null) }
            )
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
        companion object {
            fun defaultInfo() = DockInfo(listOf(*Dock.values()))
        }
    }
}