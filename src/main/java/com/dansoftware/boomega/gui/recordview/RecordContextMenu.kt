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

import com.dansoftware.boomega.export.SupportedExporters
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.keybinding.keyBinding
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.gui.util.keyCombination
import com.dansoftware.boomega.i18n.i18n
import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

class RecordContextMenu(private val recordsView: RecordsView) : ContextMenu() {

    private val itemsEmpty = Bindings.isEmpty(recordsView.table.selectionModel.selectedItems)

    init {
        buildItems()
    }

    private fun buildItems() {
        items.add(buildDeleteItem())
        items.add(buildCopyItem())
        items.add(buildCutItem())
        items.add(buildDuplicateItem())
        items.add(buildExportItem())
        items.add(SeparatorMenuItem())
        items.add(buildPasteItem())
        items.add(SeparatorMenuItem())
        items.add(buildReloadItem())
    }

    private fun buildDuplicateItem() =
        MenuItem(i18n("record.duplicate"), icon("duplicate-icon"))
            .action { recordsView.duplicateSelectedItems() }
            .keyBinding(KeyBindings.duplicateRecord)
            .apply { disableProperty().bind(itemsEmpty) }

    private fun buildDeleteItem() =
        MenuItem(i18n("record.delete"), icon("delete-icon"))
            .action { recordsView.removeSelectedItems() }
            .keyCombination(KeyBindings.deleteRecord.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }

    private fun buildCopyItem() =
        MenuItem(i18n("record.copy"), icon("copy-icon"))
            .action { recordsView.copySelectedToClipboard() }
            .keyCombination(KeyBindings.copyRecord.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }


    private fun buildCutItem() =
        MenuItem(i18n("record.cut"), icon("cut-icon"))
            .action { recordsView.cutSelectedToClipboard() }
            .keyCombination(KeyBindings.cutRecord.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }

    private fun buildPasteItem() =
        MenuItem(i18n("record.paste"), icon("paste-icon"))
            .action { recordsView.pasteItemsFromClipboard() }
            .keyCombination(KeyBindings.pasteRecord.keyCombinationProperty)
            .apply { disableProperty().bind(recordsView.clipboardEmptyProperty()) }

    private fun buildExportItem() =
        Menu(i18n("record.context_menu.export"), icon("file-export-icon")).apply {
            items.addAll(
                SupportedExporters.map { exporter ->
                    MenuItem(exporter.name, exporter.icon).action {
                        recordsView.exportSelected(exporter)
                    }
                }
            )
        }

    private fun buildReloadItem() =
        MenuItem(i18n("page.reload"), icon("reload-icon"))
            .action { recordsView.refresh() }
            .keyBinding(KeyBindings.refreshPage)

    fun applyOn(table: RecordTable) {
        table.rowContextMenu = this
        table.contextMenu = this.takeIf { table.items.isEmpty() }
        table.items.addListener(ListChangeListener {
            table.contextMenu = this.takeIf { table.items.isEmpty() }
        })
    }
}