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

package com.dansoftware.boomega.gui.clipboard

import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane

class ClipboardViewToolbar(private val view: ClipboardView) : HBox() {

    private val centerItem = buildCenterItem()

    private val actionListener = ChangeListener<RecordClipboard.Action?> { _, _, action -> onActionChanged(action) }

    init {
        padding = Insets(5.0)
        buildUI()
        initListeners()
    }

    private fun initListeners() {
        RecordClipboard.actionProperty().addListener(actionListener)
        onActionChanged(RecordClipboard.actionProperty().get())
    }

    fun releaseListeners() {
        RecordClipboard.actionProperty().removeListener(actionListener)
    }

    private fun onActionChanged(action: RecordClipboard.Action?) {
        when (action) {
            RecordClipboard.Action.COPY -> centerItem.indicateCopy()
            RecordClipboard.Action.CUT -> centerItem.indicateCut()
        }
    }

    private fun buildUI() {
        children.add(centerItem)
        children.add(buildDeleteItem())
    }

    private fun buildCenterItem() = object : StackPane() {

        init {
            setHgrow(this, Priority.ALWAYS)
        }

        fun indicateCopy() {
            children.setAll(
                Group(
                    buildHBox("copy-icon", "clipboard.view.action.copy")
                )
            )
        }

        fun indicateCut() {
            children.setAll(
                Group(
                    buildHBox("cut-icon", "clipboard.view.action.cut")
                )
            )
        }

        private fun buildHBox(iconStyleClass: String, i18n: String) =
            HBox(5.0).apply {
                children.add(StackPane(icon(iconStyleClass)))
                children.add(StackPane(Label(I18N.getValue(i18n))))
                children.add(buildCountItem())
                visibleProperty().bind(Bindings.isNotEmpty(view.table.items))
            }

        private fun buildCountItem() = Label().apply {
            textProperty().bind(
                SimpleStringProperty("(").concat(Bindings.size(view.table.items)).concat(")")
            )
        }
    }

    private fun buildDeleteItem() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("delete-icon")
        disableProperty().bind(Bindings.isEmpty(view.table.selectionModel.selectedItems))
        //TODO: tooltip
        setOnAction {
            RecordClipboard.items().removeAll(view.table.selectionModel.selectedItems)
        }
    }
}
