package com.dansoftware.boomega.gui.record.edit

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.SystemBrowser
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.dansoftware.mdeditor.MarkdownEditorControl
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Task
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.StackPane
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

    private val markdownEditor: MarkdownEditorControl = buildMarkdownEditor()
    private val changed: BooleanProperty = SimpleBooleanProperty()

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
            else -> {
                setContent(markdownEditor.also { markdownEditor.markdown = items[0].notes })
                buildChangedBinding()
            }
        }
    }

    private fun buildChangedBinding() {
        changed.bind(markdownEditor.markdownProperty().isNotEqualTo(markdownEditor.markdown))
    }

    private fun buildMarkdownEditor() = MarkdownEditorControl().apply {
        minHeight = 0.0
        setToolbarVisible(true)
        skinProperty().addListener(object : ChangeListener<Skin<*>> {
            override fun changed(observable: ObservableValue<out Skin<*>>, oldValue: Skin<*>?, newValue: Skin<*>) {
                lookup(".markdown-editor-toolbar").lookup(".right").let { it as ToolBar }.apply {
                    items.add(0,Separator(Orientation.VERTICAL))
                    items.add(0, buildSaveButton())
                }
                observable.removeListener(this)
            }
        })
        setOnLinkClicked { SystemBrowser.browse(it) }
    }

    private fun buildSaveButton() = Button().apply {
        graphic = MaterialDesignIconView(MaterialDesignIcon.CONTENT_SAVE)
        disableProperty().bind(changed.not())
        setOnAction { saveChanges() }
    }

    private fun saveChanges() {
        CachedExecutor.submit(buildSaveAction())
    }

    private fun buildSaveAction() = object : Task<Unit>() {

        init {
            setOnRunning { showProgress() }
            setOnFailed {
                stopProgress()
                logger.error("Failed to save record notes", it.source.exception)
                //TODO: error dialog
            }
            setOnSucceeded {
                stopProgress()
                buildChangedBinding()
            }
        }

        override fun call() {
            items[0].also {
                it.notes = markdownEditor.markdown
                database.updateRecord(it)
            }
        }
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