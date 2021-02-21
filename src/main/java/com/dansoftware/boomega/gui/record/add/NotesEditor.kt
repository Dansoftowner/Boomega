package com.dansoftware.boomega.gui.record.add

import com.dansoftware.boomega.i18n.I18N
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.web.HTMLEditor

private class NotesEditor : BorderPane() {

    companion object {
        const val PREFERRED_RESIZE_SPEED = 2.0
        const val PREFERRED_RESIZE_AREA_SIZE = 3.5
    }

    private val htmlEditor: HTMLEditor = HTMLEditor()

    var htmlText: String
        get() = htmlEditor.htmlText
        set(value) {
            htmlEditor.htmlText = value
        }

    init {
        this.buildBase()
        this.buildResizePolicy()
    }

    private fun buildBase() {
        this.top = buildTitleBar()
        this.center = htmlEditor
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private fun buildTitleBar() = HBox().apply {
        padding = Insets(5.0)
        children.add(Label(I18N.getValue("record.add.form.notes")))
        styleClass.add("notes-title-bar")
    }

    @Suppress("DuplicatedCode")
    private fun buildResizePolicy() {
        StackPane().let {
            bottom = it
            it.prefHeight = PREFERRED_RESIZE_AREA_SIZE
            it.cursor = Cursor.V_RESIZE
            var released = false
            var lastY: Double? = null
            it.setOnMouseReleased { released = true }
            it.setOnMouseDragged { event ->
                if (lastY === null || released) {
                    lastY = event.sceneY
                    released = false
                }
                this.prefHeight = this.height + (event.sceneY - lastY!!) * PREFERRED_RESIZE_SPEED
                lastY = event.sceneY
            }
        }
    }
}