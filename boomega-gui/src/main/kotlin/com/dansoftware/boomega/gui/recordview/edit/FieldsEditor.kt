/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.recordview.edit

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.asCentered
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.ProgressIndicator
import javafx.scene.effect.BoxBlur
import javafx.scene.layout.HBox
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
                handleNewItems(value)
            }
        }

    init {
        this.styleClass.add("record-base-editor")
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
        content.effect = BoxBlur()
        children.add(ProgressBar(ProgressIndicator.INDETERMINATE_PROGRESS).also {
            setAlignment(it, Pos.CENTER)
        })
    }

    fun stopProgress() {
        content.effect = null
        children.removeIf { it is ProgressBar }
    }

    fun changedProperty() = recordEditorForm.changedProperty()

    private fun handleNewItems(items: List<Record>) {
        buildBaseUI(this.getPreferredType(items)?.also {
            this.recordEditorForm.setItems(it, items)
        })
    }

    private fun getPreferredType(items: List<Record>) =
        items.mapNotNull(Record::type).distinct().singleOrNull()

    private fun buildBaseUI(type: Record.Type?) {
        content = when (type) {
            null -> MultipleRecordTypePlaceHolder()
            else -> recordEditorForm
        }
    }

    private class MultipleRecordTypePlaceHolder : StackPane() {
        init {
            styleClass.add("multiple-record-type-place-holder")
            children.add(buildCenterBox())
        }

        private fun buildCenterBox() = Group(
            HBox(5.0,
                icon("numeric-2-box-multiple").asCentered(),
                buildLabel().asCentered()
            )
        )

        private fun buildLabel() = Label(i18n("record.editor.placeholder.multiple_types"))
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FieldsEditor::class.java)
    }
}