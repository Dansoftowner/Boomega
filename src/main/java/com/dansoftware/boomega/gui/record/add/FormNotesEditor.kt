package com.dansoftware.boomega.gui.record.add

import com.dansoftware.boomega.i18n.I18N
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.web.HTMLEditor

private class FormNotesEditor : BorderPane() {

    private val htmlEditor: HTMLEditor = HTMLEditor()

    var htmlText: String?
        get() = htmlEditor.htmlText
        set(value) {
            htmlEditor.htmlText = value
        }

    init {
        this.buildBase()
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
}