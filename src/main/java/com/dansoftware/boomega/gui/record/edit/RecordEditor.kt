package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import jfxtras.styles.jmetro.JMetroStyleClass
import java.util.function.Consumer

class RecordEditor(
    context: Context,
    database: Database,
    items: List<Record>
) : TabPane() {

    private val fieldsEditor: FieldsEditor = FieldsEditor(context, database, items)
    private val notesEditor: NotesEditor = NotesEditor(context, database, items)

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

    var items: List<Record> = emptyList()
        set(value) {
            field = ArrayList(value)
            field.let {
                fieldsEditor.takeIf { baseEditorTab.isSelected }?.items = it
                notesEditor.takeIf { notesEditorTab.isSelected }?.items = it
            }
        }

    var onItemsModified: Consumer<List<Record>>
        get() = fieldsEditor.onItemsModified
        set(value) {
            fieldsEditor.onItemsModified = value
        }

    init {
        this.items = items
        this.styleClass.add(JMetroStyleClass.UNDERLINE_TAB_PANE)
        this.styleClass.add("record-editor")
        buildUI()
    }


    private fun buildUI() {
        this.tabs.apply {
            baseEditorTab.let(this::add)
            notesEditorTab.let(this::add)
        }
    }

    private class TabImpl(i18n: String, content: Node) : Tab(i18n.let(I18N::getValue), content) {
        init {
            this.isClosable = false
        }
    }
}