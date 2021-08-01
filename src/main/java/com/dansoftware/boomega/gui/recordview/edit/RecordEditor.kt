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

package com.dansoftware.boomega.gui.recordview.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import jfxtras.styles.jmetro.JMetroStyleClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class RecordEditor(
    context: Context,
    database: Database,
    private val selectedItems: ObservableList<Record>
) : TabPane() {

    private val fieldsEditor: FieldsEditor = FieldsEditor(context, database)
    private val notesEditor: NotesEditor = NotesEditor(context, database)

    private val baseEditorTab: Tab = TabImpl("record.editor.tab.fields", fieldsEditor).apply {
        selectedProperty().addListener { _, _, selected ->
            when {
                selected -> fieldsEditor.items = this@RecordEditor.items
            }
        }
    }

    private val notesEditorTab: Tab = TabImpl("record.property.notes", notesEditor).apply {
        selectedProperty().addListener { _, _, selected ->
            when {
                selected -> notesEditor.items = this@RecordEditor.items
            }
        }
    }

    private var items: List<Record> = emptyList()
        set(value) {
            field = ArrayList(value).also {
                fieldsEditor.takeIf { baseEditorTab.isSelected }?.items = it
                notesEditor.takeIf { notesEditorTab.isSelected }?.items = it
            }
        }

    var onItemsModified: Consumer<List<Record>>? = null

    init {
        this.styleClass.add(JMetroStyleClass.UNDERLINE_TAB_PANE)
        this.styleClass.add("record-editor")
        initListObserver(selectedItems)
        initSaveKeyCombination()
        buildUI()
        items = selectedItems
    }

    private fun initListObserver(selectedItems: ObservableList<Record>) {
        selectedItems.addListener(ListChangeListener {
            items = selectedItems
        })
    }

    private fun initSaveKeyCombination() {
        KeyBindings.saveChangesKeyBinding.also { keyBinding ->
            this.setOnKeyPressed {
                if (keyBinding.match(it)) {
                    logger.debug("Save changes key combination detected")
                    saveChanges()
                }
            }
        }
    }

    private fun buildUI() {
        this.tabs.apply {
            add(baseEditorTab)
            add(notesEditorTab)
        }
    }

    fun saveChanges() {
        if (changedProperty().get()) {
            logger.debug("RecordEditor.saveChanges() performs a save")
            fieldsEditor.persist()
            CachedExecutor.submit(SaveTask())
        }
    }

    fun changedProperty(): BooleanBinding =
        fieldsEditor.changedProperty().or(notesEditor.changedProperty()).and(Bindings.isNotEmpty(selectedItems))

    private inner class SaveTask : Task<Unit>() {
        init {
            setOnRunning {
                fieldsEditor.showProgress()
                notesEditor.showProgress()
            }

            setOnFailed {
                logger.error("Something went wrong", it.source.exception)
                fieldsEditor.stopProgress()
                notesEditor.stopProgress()
            }

            setOnSucceeded {
                fieldsEditor.updateChangedProperty()
                notesEditor.updateChangedProperty()
                fieldsEditor.stopProgress()
                notesEditor.stopProgress()
                onItemsModified?.accept(items)
            }
        }

        override fun call() {
            fieldsEditor.saveChanges()
            notesEditor.saveChanges()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RecordEditor::class.java)
    }

    private class TabImpl(i18n: String, content: Node) : Tab(I18N.getValue(i18n), content) {
        init {
            this.isClosable = false
        }
    }
}