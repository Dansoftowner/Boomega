package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FieldsEditor(
    context: Context,
    database: Database
) : StackPane() {

    private val recordEditorForm = FieldsEditorForm(context, database)

    private var content: Node
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    var items: List<Record> = emptyList()
        set(value) {
            if (field !== value) {
                field = value
                buildBaseUI(value, this.getPreferredType(value)?.also {
                    this.recordEditorForm.setItems(it, value)
                })
            }
        }

    init {
        this.styleClass.add("record-base-editor")
        content = NoSelectionPlaceHolder()
    }

    fun saveChanges() {
        recordEditorForm.saveChanges()
    }

    fun updateChangedProperty() {
        recordEditorForm.updateChangedProperty()
    }

    fun persist() {
        recordEditorForm.persist()
    }

    fun showProgress() {
        recordEditorForm.showProgress()
    }

    fun stopProgress() {
        recordEditorForm.stopProgress()
    }

    fun changedProperty() = recordEditorForm.changedProperty()

    private fun getPreferredType(items: List<Record>) = items.map(Record::recordType).distinct().singleOrNull()

    private fun buildBaseUI(items: List<Record>, type: Record.Type?) {
        content = when {
            items.isEmpty() -> NoSelectionPlaceHolder()
            type == null -> MultipleRecordTypePlaceHolder()
            else -> recordEditorForm
        }
    }

    private class MultipleRecordTypePlaceHolder : StackPane() {
        init {
            children.add(buildLabel())
        }

        private fun buildLabel() = Label(I18N.getValue("record.editor.placeholder.multiple_types")).apply {
            styleClass.add("place-holder-label")
        }
    }

    private class NoSelectionPlaceHolder : StackPane() {
        init {
            children.add(buildLabel())
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.noselection")).apply {
            styleClass.add("place-holder-label")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FieldsEditor::class.java)
    }
}