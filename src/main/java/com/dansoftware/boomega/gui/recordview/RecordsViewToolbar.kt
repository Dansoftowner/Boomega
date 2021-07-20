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

import com.dansoftware.boomega.gui.control.BaseTable
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.recordview.dock.Dock
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import org.apache.commons.lang3.StringUtils
import java.text.Collator
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors
import java.util.stream.Stream

class RecordsViewToolbar(private val view: RecordsView) : BiToolBar() {

    private val abcLocaleProp: ObjectProperty<Locale> = SimpleObjectProperty<Locale>(Locale.getDefault())
    var abcLocale: Locale
        get() = abcLocaleProp.get()
        set(value) {
            abcLocaleProp.set(value)
        }

    private lateinit var columnChooserItem: MenuButton

    init {
        buildUI()
    }

    fun updateColumnChooser() {
        columnChooserItem.items.stream()
            .map { it as TableColumnMenuItem }
            .forEach { it.isSelected = view.table.isColumnShown(it.columnType) }
    }

    private fun buildUI() {
        buildRightSide()
        buildLeftSide()
    }

    private fun buildRightSide() {
        rightItems.add(buildSearchItem())
        rightItems.add(buildSeparator())
        rightItems.add(buildInsertItem())
        rightItems.add(buildDeleteItem())
        rightItems.add(buildSeparator())
        rightItems.add(buildPasteItem())
        rightItems.add(buildCutItem())
        rightItems.add(buildCopyItem())
        rightItems.add(buildSeparator())
        rightItems.add(buildCountItem())
        rightItems.add(buildSeparator())
        rightItems.add(buildScrollToTopItem())
        rightItems.add(buildRefreshItem())
    }

    private fun buildLeftSide() {
        leftItems.add(buildColumnChooserItem().also { columnChooserItem = it })
        leftItems.add(buildColumnResetItem())
        leftItems.add(buildABCChooserItem())
        leftItems.add(buildDockSelectionItem())
    }

    private fun buildSearchItem() =
        buildToolbarItem(FontAwesomeIcon.SEARCH, "record.find") {
            view.isFindDialogVisible = view.isFindDialogVisible.not()
        }

    private fun buildInsertItem() =
        buildToolbarItem(MaterialDesignIcon.PLUS_BOX, "record.add.module.title") {
            view.insertNewRecord()
        }

    private fun buildCountItem() = Label().apply {
        padding = Insets(5.0)
        textProperty().bind(
            SimpleStringProperty(I18N.getValue("record.book.count"))
                .concat(StringUtils.SPACE)
                .concat(view.itemsCountProperty())
        )
    }

    private fun buildDeleteItem() =
        buildToolbarItem(MaterialDesignIcon.DELETE, "record.delete") {
            view.removeSelectedItems()
        }.apply {
            disableProperty().bind(Bindings.isEmpty(view.table.selectionModel.selectedItems))
        }

    private fun buildPasteItem() =
        buildToolbarItem(MaterialDesignIcon.CONTENT_PASTE, "record.paste") {
            view.pasteItemsFromClipboard()
        }.apply {
            disableProperty().bind(view.clipboardEmptyProperty())
        }

    private fun buildCopyItem() =
        buildToolbarItem(MaterialDesignIcon.CONTENT_COPY, "record.copy") {
            view.copySelectedToClipboard()
        }.apply {
            disableProperty().bind(Bindings.isEmpty(view.table.selectionModel.selectedItems))
        }

    private fun buildCutItem() =
        buildToolbarItem(MaterialDesignIcon.CONTENT_CUT, "record.cut") {
            view.cutSelectedToClipboard()
        }.apply {
            disableProperty().bind(Bindings.isEmpty(view.table.selectionModel.selectedItems))
        }

    private fun buildRefreshItem() =
        buildToolbarItem(MaterialDesignIcon.REFRESH, "page.reload") { view.refresh() }


    private fun buildScrollToTopItem() =
        buildToolbarItem(MaterialDesignIcon.BORDER_TOP, "record.table.scrolltop") { view.scrollToTop() }


    private fun buildColumnChooserItem() =
        MenuButton(
            I18N.getValue("record.table.preferred.columns"),
            FontAwesomeIconView(FontAwesomeIcon.COLUMNS)
        ).apply {
            RecordTable.columns()
                .map(::TableColumnMenuItem)
                .forEach(this.items::add)
        }

    private fun buildColumnResetItem() =
        buildToolbarItem(MaterialDesignIcon.TABLE, "record.table.colreset") {
            view.table.buildDefaultColumns()
            columnChooserItem.items.stream()
                .map { it as TableColumnMenuItem }
                .forEach { it.isSelected = it.columnType.isDefaultVisible }
        }

    private fun buildABCChooserItem() =
        MenuButton(I18N.getValue("record.table.abc")).also { toolbarItem ->
            toolbarItem.isDisable = true
            ToggleGroup().let { toggleGroup ->
                I18N.getAvailableCollators().forEach { locale, collatorSupplier ->
                    toolbarItem.items.add(AbcMenuItem(locale, collatorSupplier, toggleGroup))
                }
            }
        }

    private fun buildDockSelectionItem() =
        MenuButton(null, MaterialDesignIconView(MaterialDesignIcon.DIVISION)).apply {
            //TODO: Tooltip
            items.addAll(
                Stream.of(*Dock.values())
                    .map(::DockMenuItem)
                    .collect(Collectors.toList())
            )
        }

    private fun buildSeparator() = Separator(Orientation.VERTICAL)

    private fun buildToolbarItem(
        icon: MaterialDesignIcon,
        i18nTooltip: String,
        onClick: EventHandler<ActionEvent>
    ): Button = buildToolbarItem(MaterialDesignIconView(icon), i18nTooltip, onClick)

    private fun buildToolbarItem(
        icon: FontAwesomeIcon,
        i18nTooltip: String,
        onClick: EventHandler<ActionEvent>
    ): Button = buildToolbarItem(FontAwesomeIconView(icon), i18nTooltip, onClick)

    private fun buildToolbarItem(
        graphic: Node,
        i18nTooltip: String,
        onClick: EventHandler<ActionEvent>
    ): Button = Button(null, graphic).apply {
        onAction = onClick
        tooltip = Tooltip(I18N.getValue(i18nTooltip))
    }

    private inner class DockMenuItem(val dock: Dock) :
        CheckMenuItem(I18N.getValue(dock.i18nKey)) {

        init {
            graphic = dock.graphic
            initListener()
            setBehaviourPolicy()
        }

        private fun initListener() {
            view.docks.addListener(ListChangeListener {
                isSelected = view.docks.contains(dock)
            })
        }

        private fun setBehaviourPolicy() {
            setOnAction {
                when {
                    this.isSelected.not() ->
                        view.docks.setAll(
                            view.docks.stream()
                                .filter { it !== dock }
                                .collect(Collectors.toList())
                        )
                    else -> view.docks.add(dock)
                }
            }
        }
    }

    private inner class TableColumnMenuItem(val columnType: BaseTable.ColumnType) :
        CheckMenuItem(if (columnType.isI18N) I18N.getValue(columnType.text) else columnType.text) {

        init {
            setOnAction {
                when {
                    this.isSelected.not() -> view.table.removeColumnType(columnType)
                    else -> {
                        view.table.removeAllColumns()
                        Platform.runLater {
                            columnChooserItem.items.stream()
                                .map { it as TableColumnMenuItem }
                                .filter(TableColumnMenuItem::isSelected)
                                .map(TableColumnMenuItem::columnType)
                                .forEach(view.table::addColumnType)
                        }
                    }
                }
            }
        }
    }

    private inner class AbcMenuItem(
        val locale: Locale,
        collatorSupplier: Supplier<Collator>,
        toggleGroup: ToggleGroup
    ) : RadioMenuItem(locale.displayLanguage, MaterialDesignIconView(MaterialDesignIcon.TRANSLATE)) {
        init {
            this.selectedProperty()
                .bindBidirectional(SimpleBooleanProperty().apply { bind(abcLocaleProp.isEqualTo(locale)) })
            this.setOnAction {
                @Suppress("UNCHECKED_CAST")
                view.table.setSortingComparator(collatorSupplier.get() as Comparator<String>)
                abcLocaleProp.set(locale)
            }
            setToggleGroup(toggleGroup)
        }
    }
}