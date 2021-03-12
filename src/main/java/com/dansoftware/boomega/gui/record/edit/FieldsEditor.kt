package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class FieldsEditor(
    context: Context,
    database: Database,
    items: List<Record>
) : StackPane() {

    private val recordEditorForm = FieldsEditorForm(context, database)

    var items: List<Record> = emptyList()
        set(value) {
            if (field !== value) {
                field = value
                buildBaseUI(value, this.getPreferredType(value)?.also {
                    this.recordEditorForm.setItems(it, value)
                })
            }
        }

    var onItemsModified: Consumer<List<Record>>
        get() = recordEditorForm.onItemsModified.get()
        set(value) {
            recordEditorForm.onItemsModified.set(value)
        }

    init {
        this.styleClass.add("record-base-editor")
        this.items = items
        this.initKeyPressedPolicy()
    }

    private fun initKeyPressedPolicy() {
        KeyBindings.saveChangesKeyBinding.also { keyBinding ->
            this.setOnKeyPressed {
                if (keyBinding.match(it)) {
                    logger.debug("Save changes key combination detected")
                    recordEditorForm.saveChanges()
                }
            }
        }
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
        val logger: Logger = LoggerFactory.getLogger(FieldsEditor::class.java)
    }
}