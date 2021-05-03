package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.mdeditor.MarkdownEditorControl
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NotesEditor(
    private val context: Context,
    private val database: Database
) : StackPane() {

    var items: List<Record> = emptyList()
        set(value) {
            if (field !== value) {
                field = value
                handleItemsChanged(value)
            }
        }

    private val markdownEditor: MarkdownEditorControl = buildMarkdownEditor()
    private val changed: BooleanProperty = SimpleBooleanProperty()

    private var content: Node
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    init {
        this.styleClass.add("notes-editor")
        this.minHeight = 0.0
        content = NoSelectionPlaceHolder()
    }

    fun showProgress() {
        this.children.add(ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS))
    }

    fun stopProgress() {
        this.children.removeIf { it is ProgressIndicator }
    }

    private fun handleItemsChanged(items: List<Record>) {
        when {
            items.isEmpty() -> content = NoSelectionPlaceHolder()
            items.size > 1 -> content = MultipleSelectionPlaceHolder()
            else ->  {
                content = markdownEditor.also { markdownEditor.markdown = items[0].notes ?: "" }
                updateChangedProperty()
            }
        }
    }

    fun updateChangedProperty() {
        changed.bind(
            markdownEditor
                .markdownProperty()
                .isNotEqualTo(markdownEditor.markdown)
        )
    }

    private fun buildMarkdownEditor() = MarkdownEditorControl().apply {
        minHeight = 0.0
        setToolbarVisible(true)
        setOnLinkClicked(SystemBrowser::browse)
    }

    fun saveChanges() {
        items[0].also {
            it.notes = markdownEditor.markdown
            database.updateRecord(it)
        }
    }

    fun changedProperty() = changed

    private class NoSelectionPlaceHolder : StackPane() {
        init {
            children.add(buildLabel())
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.noselection")).apply {
            styleClass.add("place-holder-label")
        }
    }

    private class MultipleSelectionPlaceHolder : StackPane() {
        init {
            children.add(buildLabel())
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.multiple")).apply {
            styleClass.add("place-holder-label")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(NotesEditor::class.java)
    }

}