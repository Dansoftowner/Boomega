package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableBooleanValue
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

    private val notesEditorTab: Tab = TabImpl("record.editor.tab.notes", notesEditor).apply {
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
            fieldsEditor.takeIf { it.changedProperty().get() }?.saveChanges()
            notesEditor.takeIf { it.changedProperty().get() }?.saveChanges()
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