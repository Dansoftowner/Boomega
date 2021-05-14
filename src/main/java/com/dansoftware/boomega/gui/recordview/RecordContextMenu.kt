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

import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.keyCombination
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.binding.Bindings
import javafx.collections.ListChangeListener
import javafx.scene.control.ContextMenu
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
        items.add(SeparatorMenuItem())
        items.add(buildPasteItem())
    }

    private fun buildDeleteItem() =
        MenuItem(I18N.getValue("record.delete"), MaterialDesignIconView(MaterialDesignIcon.DELETE))
            .action { recordsView.removeSelectedItems() }
            .keyCombination(KeyBindings.deleteRecordKeyBinding.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }

    private fun buildCopyItem() =
        MenuItem(I18N.getValue("record.copy"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY))
            .action { recordsView.copySelectedToClipboard() }
            .keyCombination(KeyBindings.copyRecordKeyBinding.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }


    private fun buildCutItem() =
        MenuItem(I18N.getValue("record.cut"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_CUT))
            .action { recordsView.cutSelectedToClipboard() }
            .keyCombination(KeyBindings.cutRecordKeyBinding.keyCombinationProperty)
            .apply { disableProperty().bind(itemsEmpty) }

    private fun buildPasteItem() =
        MenuItem(I18N.getValue("record.paste"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_PASTE))
            .action {  recordsView.pasteItemsFromClipboard() }
            .keyCombination(KeyBindings.pasteRecordKeyBinding.keyCombinationProperty)
            .apply { disableProperty().bind(RecordClipboard.emptyProperty()) }

    fun applyOn(table: RecordTable) {
        table.rowContextMenu = this
        table.contextMenu = this.takeIf { table.items.isEmpty() }
        table.items.addListener(ListChangeListener {
            table.contextMenu = this.takeIf { table.items.isEmpty() }
        })
    }
}