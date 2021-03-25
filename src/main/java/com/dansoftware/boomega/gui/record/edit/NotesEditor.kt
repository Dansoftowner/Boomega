package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.concurrent.Task
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.StackPane
import javafx.scene.web.HTMLEditor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NotesEditor(
    private val context: Context,
    private val  database: Database,
    items: List<Record>
) : StackPane() {

    var items: List<Record> = emptyList()
    set(value) {
        if (field !== value) {
            field = value
            handleItemsChanged(value)
        }
    }

    private val htmlEditor: HTMLEditor = buildHTMLEditor()

    init {
        this.styleClass.add("notes-editor")
        this.items = items
        this.minHeight = 0.0
        this.initKeyPressedPolicy()
    }

    private fun setContent(content: Node) {
        children.setAll(content)
    }

    private fun initKeyPressedPolicy() {
        KeyBindings.saveChangesKeyBinding.keyCombination.also { keyCombi ->
            this.setOnKeyPressed { event ->
                if (keyCombi.match(event)) {
                    saveChanges()
                }
            }
        }
    }

    private fun showProgress() {
        this.children.add(ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS))
    }

    private fun stopProgress() {
        this.children.removeIf { it is ProgressIndicator }
    }

    private fun handleItemsChanged(items: List<Record>) {
        when {
            items.isEmpty() -> setContent(NoSelectionPlaceHolder())
            items.size > 1 -> setContent(MultipleSelectionPlaceHolder())
            else -> setContent(htmlEditor.also { fillHTMLEditor(it, items[0]) })
        }
    }

    private fun fillHTMLEditor(htmlEditor: HTMLEditor, record: Record) {
        htmlEditor.htmlText = record.notes
    }

    private fun buildHTMLEditor() = HTMLEditor().apply {
        minHeight = 0.0
        lookup(".top-toolbar").let { it as ToolBar }.apply {
            items.add(0, Separator(Orientation.VERTICAL))
            items.add(0, buildSaveButton())
        }
    }

    private fun buildSaveButton() = Button().apply {
        graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE)
        setOnAction { saveChanges() }
    }

    private fun saveChanges() {
        CachedExecutor.submit(object : Task<Unit>() {

            init {
                setOnRunning { showProgress() }
                setOnFailed {
                    stopProgress()
                    logger.error("Failed to save record notes", it.source.exception)
                    //TODO: error dialog
                }
                setOnSucceeded {
                    stopProgress()
                }
            }

            override fun call() {
                items[0].also {
                    it.notes = htmlEditor.htmlText
                    database.updateRecord(it)
                }
            }
        })
    }

    private class NoSelectionPlaceHolder : StackPane() {
        init {
            buildLabel().let(children::add)
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.noselection")).apply {
            styleClass.add("place-holder-label")
        }
    }

    private class MultipleSelectionPlaceHolder : StackPane() {
        init {
            buildLabel().let(children::add)
        }

        private fun buildLabel() = Label(I18N.getValue("google.books.dock.placeholder.multiple")).apply {
            styleClass.add("place-holder-label")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(NotesEditor::class.java)
    }

}