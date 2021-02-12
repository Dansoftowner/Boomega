package com.dansoftware.libraryapp.gui.record.show.dock.editor

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.i18n.I18N
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.TextAlignment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class RecordEditor(
    context: Context,
    database: Database,
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
        buildBaseUI(items, this.getPreferredType(items)?.also {
            this.recordEditorForm.setItems(it, items)
        })
    }

    private fun getPreferredType(items: List<Record>) = items.map(Record::recordType).distinct().singleOrNull()

    private fun buildBaseUI(items: List<Record>, type: Record.Type?) {
        when {
            items.isEmpty() -> children.setAll(EmptyPlaceHolder())
            type == null -> children.setAll(MultipleRecordTypePlaceHolder())
            else -> children.setAll(recordEditorForm)
        }
    }


    private class MultipleRecordTypePlaceHolder : StackPane() {
        init {
            buildLabel().let(children::add)
        }

        private fun buildLabel() = Label(I18N.getValue("record.editor.placeholder.multiple_types")).apply {
            styleClass.add("place-holder-label")
        }
    }

    private class EmptyPlaceHolder : StackPane() {
        init {
            buildLabel().let(children::add)
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.noselection")).apply {
            styleClass.add("place-holder-label")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RecordEditor::class.java)
    }
}