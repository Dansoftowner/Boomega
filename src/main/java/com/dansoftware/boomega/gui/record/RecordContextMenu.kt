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

package com.dansoftware.boomega.gui.record

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.collections.ObservableList
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import java.util.function.Consumer

class RecordContextMenu(
    private val records: ObservableList<Record>,
    private val deleteAction: Consumer<List<Record>>,
    private val copyAction: Consumer<List<Record>>,
    private val cutAction: Consumer<List<Record>>,
    private val pasteAction: Runnable,
    private val pasteItemDisable: ReadOnlyBooleanProperty?
) : ContextMenu() {

    private val itemsEmpty = Bindings.isEmpty(records)

    init {
        buildItems()
    }

    private fun buildItems() {
        buildDeleteItem()
        buildCopyItem()
        buildCutItem()
        buildSeparator()
        buildPasteItem()
    }

    private fun buildDeleteItem() {
        MenuItem(I18N.getValue("record.delete"), MaterialDesignIconView(MaterialDesignIcon.DELETE)).apply {
            setOnAction { deleteAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.deleteRecordKeyBinding.keyCombinationProperty)
            disableProperty().bind(itemsEmpty)
        }.let(items::add)
    }

    private fun buildCopyItem() {
        MenuItem(I18N.getValue("record.copy"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_COPY)).apply {
            setOnAction { copyAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.copyRecordKeyBinding.keyCombinationProperty)
            disableProperty().bind(itemsEmpty)
        }.let(items::add)
    }

    private fun buildCutItem() {
        MenuItem(I18N.getValue("record.cut"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_CUT)).apply {
            setOnAction { cutAction.accept(records.let(::ArrayList)) }
            acceleratorProperty().bind(KeyBindings.cutRecordKeyBinding.keyCombinationProperty)
            disableProperty().bind(itemsEmpty)
        }.let(items::add)
    }

    private fun buildPasteItem() {
        MenuItem(I18N.getValue("record.paste"), MaterialDesignIconView(MaterialDesignIcon.CONTENT_PASTE)).apply {
            setOnAction { pasteAction.run() }
            acceleratorProperty().bind(KeyBindings.pasteRecordKeyBinding.keyCombinationProperty)
            pasteItemDisable?.let(disableProperty()::bind)
        }.let(items::add)
    }

    private fun buildSeparator() {
        items.add(SeparatorMenuItem())
    }

    class Builder(private val records: ObservableList<Record>) {
        private var deleteAction: Consumer<List<Record>>? = null
        private var cutAction: Consumer<List<Record>>? = null
        private var copyAction: Consumer<List<Record>>? = null
        private var pasteAction: Runnable? = null
        private var pasteItemDisablePolicy: ReadOnlyBooleanProperty? = null

        fun deleteAction(deleteAction: Consumer<List<Record>>) = this.apply {
            this.deleteAction = deleteAction
        }

        fun cutAction(cutAction: Consumer<List<Record>>) = this.apply {
            this.cutAction = cutAction
        }

        fun copyAction(copyAction: Consumer<List<Record>>) = this.apply {
            this.copyAction = copyAction
        }

        fun pasteAction(pasteAction: Runnable?) = this.apply {
            this.pasteAction = pasteAction
        }

        fun pasteItemDisablePolicy(binding: ReadOnlyBooleanProperty) = this.apply {
            this.pasteItemDisablePolicy = binding
        }

        fun build(): RecordContextMenu =
            RecordContextMenu(
                records,
                deleteAction ?: Consumer { },
                copyAction ?: Consumer { },
                cutAction ?: Consumer { },
                pasteAction ?: Runnable { },
                pasteItemDisablePolicy
            )
    }

    companion object {
        @JvmStatic
        fun builder(records: ObservableList<Record>): Builder = records.let(RecordContextMenu::Builder)
    }

}