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
import com.dansoftware.boomega.export.SupportedExporters
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.control.KeyBindingTooltip
import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.recordview.config.RecordsViewConfigurationOverlay
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableBooleanValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*

class RecordsViewToolbar(
    private val context: Context,
    private val view: RecordsView,
    private val preferences: Preferences
) : BiToolBar() {

    init {
        styleClass("records-view-toolbar")
        buildUI()
    }

    private fun buildUI() {
        buildLeftSide()
        buildRightSide()
    }

    private fun buildLeftSide() {
        leftItems.add(buildRefreshItem())
        leftItems.add(Separator())
        leftItems.add(buildCountItem())
        leftItems.add(Separator())
        leftItems.add(buildExportItem())
        leftItems.add(Separator())
        leftItems.add(buildCopyItem())
        leftItems.add(buildCutItem())
        leftItems.add(buildPasteItem())
        leftItems.add(buildDuplicateItem())
        leftItems.add(Separator())
        leftItems.add(buildDeleteItem())
        leftItems.add(buildInsertItem())
        leftItems.add(Separator())
        leftItems.add(buildSearchItem())
    }

    private fun buildRightSide() {
        rightItems.add(buildOptionsItem())
    }

    private fun buildSearchItem() =
        buildToolbarItem(
            "search-icon",
            "record.find",
            KeyBindings.findRecord
        ) { view.isFindDialogVisible = view.isFindDialogVisible.not() }

    private fun buildInsertItem() =
        buildToolbarItem(
            "plus-box-icon",
            "record.add.module.title",
            view.findDialogVisibleProperty
        ) { view.insertNewRecord() }

    private fun buildCountItem() = Label().apply {
        padding = Insets(5.0)
        textProperty().bind(
            SimpleStringProperty("")
                .concat(view.selectedItemsCount())
                .concat("/")
                .concat(view.itemsCountProperty())
                .concat(" ")
                .concat(I18N.getValue("record.book.items_selected"))
        )
    }

    private fun buildDeleteItem() =
        buildToolbarItem(
            "delete-icon",
            "record.delete",
            KeyBindings.deleteRecord,
            view.table.selectedItems.emptyBinding()
        ) { view.removeSelectedItems() }

    private fun buildPasteItem() =
        buildToolbarItem(
            "paste-icon",
            "record.paste",
            KeyBindings.pasteRecord,
            view.clipboardEmptyProperty()
        ) { view.pasteItemsFromClipboard() }

    private fun buildCopyItem() =
        buildToolbarItem(
            "copy-icon",
            "record.copy",
            KeyBindings.copyRecord,
            view.table.selectedItems.emptyBinding()
        ) { view.copySelectedToClipboard() }

    private fun buildDuplicateItem() =
        buildToolbarItem(
            "duplicate-icon",
            "record.duplicate",
            KeyBindings.duplicateRecord,
            view.table.selectedItems.emptyBinding()
        ) { view.duplicateSelectedItems() }

    private fun buildCutItem() =
        buildToolbarItem(
            "cut-icon",
            "record.cut",
            KeyBindings.cutRecord,
            view.table.selectedItems.emptyBinding()
        ) { view.cutSelectedToClipboard() }

    private fun buildRefreshItem() =
        buildToolbarItem(
            "reload-icon",
            "page.reload",
            KeyBindings.refreshPage
        ) { view.refresh() }

    private fun buildExportItem() =
        MenuButton(null, icon("file-export-icon")).apply {
            styleClass("options-item")
            tooltip = Tooltip(i18n("record.export"))
            disableProperty().bind(view.table.selectedItems.emptyBinding())
            items.addAll(SupportedExporters.map {
                MenuItem(it.name, it.icon).action { _ ->
                    view.exportSelected(it)
                }.apply { isDisable = it.isDisabled }
            })
        }

    private fun buildOptionsItem() =
        buildToolbarItem("tune-icon", "record.panel_config") {
            context.showOverlay(RecordsViewConfigurationOverlay(view, preferences))
        }

    private fun buildToolbarItem(
        iconStyleClass: String,
        i18nTooltip: String,
        onClick: EventHandler<ActionEvent>
    ): Button = buildToolbarItem(icon(iconStyleClass), i18nTooltip, onClick)

    private fun buildToolbarItem(
        iconStyleClass: String,
        i18nTooltip: String,
        disable: ObservableBooleanValue,
        onClick: EventHandler<ActionEvent>
    ): Button = buildToolbarItem(icon(iconStyleClass), i18nTooltip, onClick).apply {
        disableProperty().bind(disable)
    }

    private fun buildToolbarItem(
        graphic: Node,
        i18nTooltip: String,
        onClick: EventHandler<ActionEvent>
    ): Button = Button(null, graphic).apply {
        onAction = onClick
        tooltip = Tooltip(I18N.getValue(i18nTooltip))
    }

    private fun buildToolbarItem(
        iconStyleClass: String,
        i18nTooltip: String,
        keyBinding: KeyBinding,
        disable: ObservableBooleanValue? = null,
        onClick: EventHandler<ActionEvent>
    ): Button = Button(null, icon(iconStyleClass)).apply {
        onAction = onClick
        tooltip = KeyBindingTooltip(i18n(i18nTooltip), keyBinding)
        disable?.let(disableProperty()::bind)
    }
}