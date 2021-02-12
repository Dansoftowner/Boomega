package com.dansoftware.libraryapp.gui.record.show.dock.editor

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class RecordEditor(
    private val context: Context,
    private val database: Database,
    items: List<Record>
) : StackPane() {

    private val recordEditorForm = RecordEditorForm(context, database)

    var onItemsModified: Consumer<List<Record>>
        get() = recordEditorForm.onItemsModified.get()
        set(value) {
            recordEditorForm.onItemsModified.set(value)
        }

    var onItemsDeleted: Consumer<List<Record>>
        get() = recordEditorForm.onItemsDeleted.get()
        set(value) {
            recordEditorForm.onItemsDeleted.set(value)
        }


    init {
        this.styleClass.add("record-editor")
        this.setItems(items)
    }

    fun setItems(items: List<Record>) {
        buildBaseUI(this.getPreferredType(items)?.also {
            this.recordEditorForm.setItems(it, items)
        })
    }

    private fun getPreferredType(items: List<Record>) = items.map(Record::recordType).distinct().singleOrNull()

    private fun buildBaseUI(type: Record.Type?) {
        when (type) {
            null -> children.setAll(MultipleRecordTypePlaceHolder())
            else -> children.setAll(recordEditorForm)
        }
    }


    private class MultipleRecordTypePlaceHolder : StackPane() {
        init {

        }
    }

    private class MultipleSelectionPlaceHolder : StackPane() {

    }

    private class EmptyPlaceHolder : StackPane() {

    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RecordEditor::class.java)
    }
}