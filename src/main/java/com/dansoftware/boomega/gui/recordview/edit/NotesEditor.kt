package com.dansoftware.boomega.gui.recordview.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.recordview.util.MultipleSelectionPlaceHolder
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.mdeditor.MarkdownEditorControl
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.effect.BoxBlur
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

    private var content: Node?
        get() = children.getOrNull(0)
        set(value) {
            children.setAll(value)
        }

    init {
        this.styleClass.add("notes-editor")
        this.minHeight = 0.0
    }

    fun showProgress() {
        content?.effect = BoxBlur()
        children.add(ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS).also {
            setAlignment(it, Pos.CENTER)
        })
    }

    fun stopProgress() {
        content?.effect = null
        children.removeIf { it is ProgressBar }
    }

    private fun handleItemsChanged(items: List<Record>) {
        when {
            items.isEmpty() -> children.clear()
            items.size > 1 -> content = MultipleSelectionPlaceHolder()
            else -> {
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
        items.takeIf { it.isNotEmpty() }?.get(0)?.also {
            it.notes = markdownEditor.markdown
            database.updateRecord(it)
        }
    }

    fun changedProperty() = changed

    companion object {
        val logger: Logger = LoggerFactory.getLogger(NotesEditor::class.java)
    }

}