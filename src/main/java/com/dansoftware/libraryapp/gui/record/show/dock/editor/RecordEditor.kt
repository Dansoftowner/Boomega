package com.dansoftware.libraryapp.gui.record.show.dock.editor

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.record.RecordValues
import com.dansoftware.libraryapp.gui.util.FixedFontMaterialDesignIconView
import com.dansoftware.libraryapp.gui.util.applyOnTextField
import com.dansoftware.libraryapp.gui.util.formsfx.SimpleRatingControl
import com.dansoftware.libraryapp.i18n.I18N
import com.dansoftware.libraryapp.util.concurrent.ExploitativeExecutor
import com.dlsc.formsfx.model.structure.*
import com.dlsc.formsfx.model.util.ResourceBundleService
import com.dlsc.formsfx.view.controls.SimpleTextControl
import com.dlsc.formsfx.view.renderer.FormRenderer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.concurrent.Task
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecordEditor(
    private val context: Context,
    private val database: Database,
    items: List<Record>
) : StackPane() {


    private val recordEditorForm = RecordEditorForm(context, database)


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