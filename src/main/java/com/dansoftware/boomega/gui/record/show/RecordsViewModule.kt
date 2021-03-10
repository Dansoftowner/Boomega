package com.dansoftware.boomega.gui.record.show

import com.dansoftware.boomega.appdata.Preferences
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.context.NotifiableModule
import com.dansoftware.boomega.gui.record.RecordClipboard
import com.dansoftware.boomega.gui.record.show.RecordsView.Dock
import com.dansoftware.boomega.gui.record.show.RecordsView.DockInfo
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.ExploitativeExecutor
import com.dlsc.workbenchfx.model.WorkbenchModule
import com.dlsc.workbenchfx.view.controls.ToolbarItem
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.concurrent.Task
import javafx.concurrent.WorkerStateEvent
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.text.Collator
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

class RecordsViewModule(
    private val context: Context,
    private val preferences: Preferences,
    private val database: Database
) : WorkbenchModule(I18N.getValue("record.book.view.module.name"), MaterialDesignIcon.LIBRARY),
    NotifiableModule<RecordsViewModule.Message?> {

    class Message(val records: List<Record>, val action: Action) {
        constructor(record: Record, action: Action) : this(listOf(record), action)

        enum class Action {
            DELETED, INSERTED, UPDATED
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RecordsViewModule::class.java)

        private val colConfigKey =
            Preferences.Key("books.view.table.columns", TableColumnsInfo::class.java) { TableColumnsInfo.byDefault() }

        private val abcConfigKey =
            Preferences.Key("books.view.module.table.abcsort", Locale::class.java) { Locale.getDefault() }

        private val docksConfigKey =
            Preferences.Key("books.view.dock.info", DockInfo::class.java) { DockInfo.defaultInfo() }
    }

    private val content: ObjectProperty<RecordsView> =
        object : SimpleObjectProperty<RecordsView>() {
            override fun invalidated() {
                get()?.let { onNewContentCreated(it) }
            }
        }

    private val copyHandle = Any()

    private val abcLocale: ObjectProperty<Locale> = SimpleObjectProperty()
    private var columnChooserItem: ToolbarItem? = null

    private val table: RecordTable
        get() = content.get().booksTable

    init {
        readBaseConfig()
        buildToolbar()
    }

    override fun activate(): Node =
        content.get() ?: RecordsView(context, database).also(content::set)

    override fun destroy(): Boolean = true.also {
        writeConfig()
        content.set(null)
    }

    override fun commitData(data: Message?) {
        content.get()?.let {
            when (data?.action) {
                Message.Action.DELETED -> {
                    table.items.removeAll(data.records)
                }
                Message.Action.INSERTED -> {
                    table.items.addAll(data.records)
                }
                Message.Action.UPDATED -> {
                }
            }
            table.refresh()
        }
    }

    private fun readBaseConfig() {
        abcLocale.set(preferences.get(abcConfigKey))
    }

    private fun buildToolbar() {
        toolbarControlsRight.add(buildSeparator())
        toolbarControlsRight.add(buildPasteItem())
        toolbarControlsRight.add(buildSeparator())
        toolbarControlsRight.add(buildCutItem())
        toolbarControlsRight.add(buildCopyItem())
        toolbarControlsRight.add(buildDeleteItem())
        toolbarControlsRight.add(buildSeparator())
        toolbarControlsRight.add(buildCountItem())
        toolbarControlsRight.add(buildSeparator())
        toolbarControlsRight.add(buildRefreshItem())
        toolbarControlsRight.add(buildScrollToTopItem())
        toolbarControlsLeft.add(buildColumnChooserItem().also { columnChooserItem = it })
        toolbarControlsLeft.add(buildColumnResetItem())
        toolbarControlsLeft.add(buildABCChooserItem())
        toolbarControlsLeft.add(buildDockSelectionItem())
    }

    private fun onNewContentCreated(view: RecordsView) {
        buildTableRowContextMenu(view.booksTable)
        readColumnConfigurations(view.booksTable)
        loadBooks(view)
        readDockConfigurations(view)
    }

    private fun buildTableRowContextMenu(table: RecordTable) {
        RecordContextMenu.builder(table.selectionModel.selectedItems)
            .deleteAction(this::invokeRemoveAction)
            .copyAction(this::invokeCopyAction)
            .cutAction(this::invokeCutAction)
            .pasteItemDisablePolicy(RecordClipboard.emptyProperty())
            .pasteAction { RecordClipboard.pullContent().items.let(this::invokePasteAction) }
            .build()
            .let {
                table.rowContextMenu = it
                table.contextMenu = it.takeIf { table.items.isEmpty() }
                table.items.addListener(ListChangeListener { _ -> table.contextMenu = it.takeIf { table.items.isEmpty() } })
            }
    }

    private fun readColumnConfigurations(table: RecordTable) {
        preferences.get(colConfigKey).columnTypes.forEach(table::addColumn)
        columnChooserItem!!.items.stream()
            .map { it as TableColumnMenuItem }
            .forEach { it.isSelected = table.isColumnShown(it.columnType) }
    }

    private fun readDockConfigurations(content: RecordsView) {
        content.dockInfo = preferences.get(docksConfigKey)
    }

    private fun loadBooks(content: RecordsView) {
        TableRecordsGetTask(
            context,
            content.booksTable,
            database
        ).apply {
           addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED) {
                content.setDockFullyResizable()
           }
        }.let(ExploitativeExecutor::submit)
    }

    private fun writeConfig() {
        preferences.editor()
            .put(colConfigKey, TableColumnsInfo(table.showingColumns))
            .put(abcConfigKey, abcLocale.get())
            .put(docksConfigKey, content.get().dockInfo)
    }

    private fun buildCountItem(): ToolbarItem = ToolbarItem().also { toolbarItem ->
        content.addListener { _, _, newContent ->
            newContent?.let {
                toolbarItem.textProperty().bind(
                    SimpleStringProperty(I18N.getValue("record.book.count"))
                        .concat(StringUtils.SPACE)
                        .concat(Bindings.size(newContent.booksTable.items))
                )
            } ?: toolbarItem.textProperty().unbind()
        }
    }

    private fun buildDeleteItem(): ToolbarItem =
        ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.DELETE)).also { item ->
            //TODO: item.setTooltip();
            content.addListener { _, _, newContent: RecordsView? ->
                newContent?.let {
                    val selectedItems = newContent.booksTable.selectionModel.selectedItems
                    item.disableProperty().bind(Bindings.isEmpty(selectedItems))
                    item.setOnClick {
                        //TODO: showing confirmation dialog
                        invokeRemoveAction(ArrayList(selectedItems))
                    }
                } ?: item.disableProperty().unbind()
            }
        }

    private fun buildPasteItem(): ToolbarItem =
        ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.CONTENT_PASTE)).also { item ->
            RecordClipboard.emptyProperty()
                .or(RecordClipboard.identifierProperty().isEqualTo(copyHandle))
                .let(item.disableProperty()::bind)
            item.setOnClick { RecordClipboard.pullContent().items.let(this::invokePasteAction) }
        }

    private fun buildCopyItem(): ToolbarItem =
        ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY)).also { item ->
            //TODO: tooltip
            content.addListener { _, _, newContent: RecordsView? ->
                newContent?.let {
                    val selectedItems = newContent.booksTable.selectionModel.selectedItems
                    item.disableProperty().bind(Bindings.isEmpty(selectedItems))
                    item.setOnClick {
                        //TODO: showing confirmation dialog
                        invokeCopyAction(ArrayList(selectedItems))
                    }
                } ?: item.disableProperty().unbind()
            }
        }

    private fun buildCutItem(): ToolbarItem =
        ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.CONTENT_CUT)).also { item ->
            //TODO: tooltip
            content.addListener { _, _, newContent: RecordsView? ->
                newContent?.let {
                    val selectedItems = newContent.booksTable.selectionModel.selectedItems
                    item.disableProperty().bind(Bindings.isEmpty(selectedItems))
                    item.setOnClick {
                        //TODO: showing confirmation dialog
                        invokeCutAction(ArrayList(selectedItems))
                    }
                } ?: item.disableProperty().unbind()
            }
        }

    private fun invokePasteAction(items: List<Record>) {
        object : Task<Unit>() {
            init {
                setOnFailed { e ->
                    //TODO: error dialog
                    context.stopProgress()
                    logger.error("Couldn't paste record elements", e.source.exception)
                }
                setOnRunning { context.showIndeterminateProgress() }
                setOnSucceeded {
                    context.stopProgress()
                    table.items.addAll(items)
                }
            }

            override fun call() {
                synchronized(RecordClipboard) {
                    logger.debug("Performing paste action....")
                    items.stream()
                        .map(Record::copy)
                        .peek { it.id = null }
                        .forEach(database::insertRecord)
                }
            }
        }.let(ExploitativeExecutor::submit)
    }

    private fun invokeCutAction(items: List<Record>) {
        RecordClipboard.pushItems(copyHandle, RecordClipboard.Action.CUT, items).also { push ->
            push.onPulled { invokeRemoveAction(it.items) }
        }
    }

    private fun invokeCopyAction(items: List<Record>) {
        RecordClipboard.pushItems(copyHandle, RecordClipboard.Action.COPY, items)
    }

    private fun invokeRemoveAction(items: List<Record>) {
        ExploitativeExecutor.submit(buildRemoveAction(items))
    }

    private fun buildRemoveAction(items: List<Record>): Task<Unit> = object : Task<Unit>() {

        init {
            setOnRunning { context.showIndeterminateProgress() }
            setOnSucceeded {
                context.stopProgress()
                table.items.removeAll(items)
            }
            setOnFailed { context.stopProgress() }
        }

        override fun call() {
            synchronized(RecordClipboard) {
                logger.debug("Performing remove action...")
                items.forEach(database::removeRecord)
            }
        }
    }

    private fun refresh(): Unit = loadBooks(getContent())

    private fun buildRefreshItem(): ToolbarItem {
        return buildToolbarItem(MaterialDesignIcon.REFRESH, "record.toolbar.refresh") { refresh() }
    }

    private fun buildScrollToTopItem(): ToolbarItem {
        return buildToolbarItem(MaterialDesignIcon.BORDER_TOP, "record.table.scrolltop") { getContent().scrollToTop() }
    }

    private fun buildColumnChooserItem(): ToolbarItem =
        ToolbarItem(
            I18N.getValue("record.table.preferred.columns"),
            FontAwesomeIconView(FontAwesomeIcon.COLUMNS)
        ).apply {
            Stream.of(*RecordTable.ColumnType.values())
                .map(::TableColumnMenuItem)
                .forEach(this.items::add)
        }

    private fun buildColumnResetItem(): ToolbarItem =
        buildToolbarItem(MaterialDesignIcon.TABLE, "record.table.colreset") {
            table.buildDefaultColumns()
            columnChooserItem!!.items.stream()
                .map { it as TableColumnMenuItem }
                .forEach { it.isSelected = it.columnType.isDefaultVisible }
        }

    private fun buildABCChooserItem(): ToolbarItem =
        ToolbarItem(I18N.getValue("record.table.abc")).also { toolbarItem ->
            ToggleGroup().let { toggleGroup ->
                I18N.getAvailableCollators().forEach { locale: Locale, collatorSupplier: Supplier<Collator> ->
                    toolbarItem.items.add(
                        AbcMenuItem(locale, collatorSupplier, toggleGroup)
                    )
                }
            }
        }

    private fun buildDockSelectionItem(): ToolbarItem =
        ToolbarItem(MaterialDesignIconView(MaterialDesignIcon.DIVISION)).also { toolbarItem ->
            //TODO: Tooltip
            Stream.of(*Dock.values())
                .map(::DockMenuItem)
                .collect(Collectors.toList())
                .let(toolbarItem.items::addAll)
        }

    private fun buildSeparator(): ToolbarItem {
        return ToolbarItem(Separator(Orientation.VERTICAL))
    }

    private fun buildToolbarItem(
        icon: MaterialDesignIcon,
        i18nTooltip: String,
        onClick: EventHandler<MouseEvent>
    ): ToolbarItem = ToolbarItem(MaterialDesignIconView(icon), onClick).apply {
        tooltip = Tooltip(I18N.getValue(i18nTooltip))
    }

    private fun getContent(): RecordsView = content.get()

    private inner class DockMenuItem(private val dock: Dock) :
        CheckMenuItem(I18N.getValue(dock.i18nKey)) {

        private fun initContentListener() {
            content.addListener(object : ChangeListener<RecordsView?> {
                override fun changed(
                    observable: ObservableValue<out RecordsView?>,
                    oldValue: RecordsView?,
                    recordsView: RecordsView?
                ) {
                    if (recordsView != null) {
                        this@DockMenuItem.isSelected = recordsView.docks.contains(dock)
                        observable.removeListener(this)
                    }
                }
            })
        }

        private fun setBehaviourPolicy() {
            onAction = EventHandler { e: ActionEvent? ->
                if (!this.isSelected) {
                    getContent().docks
                        .setAll(getContent()
                            .docks
                            .stream()
                            .filter { dck: Dock -> dck !== dock }
                            .collect(Collectors.toList())
                        )
                } else {
                    getContent().docks.add(dock)
                }
            }
        }

        init {
            graphic = dock.graphic
            initContentListener()
            setBehaviourPolicy()
        }
    }

    private inner class TableColumnMenuItem(val columnType: RecordTable.ColumnType) :
        CheckMenuItem(I18N.getValue(columnType.i18Nkey)) {

        init {
            setOnAction {
                when {
                    this.isSelected.not() -> table.removeColumn(columnType)
                    else -> {
                        table.removeAllColumns()
                        columnChooserItem!!.items.stream()
                            .map { it as TableColumnMenuItem }
                            .filter(TableColumnMenuItem::isSelected)
                            .map(TableColumnMenuItem::columnType)
                            .forEach(table::addColumn)
                    }
                }
            }
        }
    }

    private inner class AbcMenuItem(
        private val locale: Locale,
        collatorSupplier: Supplier<Collator>,
        toggleGroup: ToggleGroup
    ) : RadioMenuItem(locale.displayLanguage, MaterialDesignIconView(MaterialDesignIcon.TRANSLATE)) {
        init {
            userData = locale
            this.isSelected = locale == abcLocale.get()
            selectedProperty().addListener { _, _, selected ->
                if (selected) {
                    @Suppress("UNCHECKED_CAST")
                    table.setSortingComparator(collatorSupplier.get() as Comparator<String>)
                    abcLocale.set(locale)
                }
            }
            setToggleGroup(toggleGroup)
        }
    }

    /**
     * Used for storing the preferred table columns in the configurations.
     */
    internal class TableColumnsInfo(val columnTypes: List<RecordTable.ColumnType>) {
        companion object {
            fun byDefault(): TableColumnsInfo =
                TableColumnsInfo(
                    Arrays.stream(RecordTable.ColumnType.values())
                        .filter(RecordTable.ColumnType::isDefaultVisible)
                        .collect(Collectors.toList())
                )
        }
    }
}