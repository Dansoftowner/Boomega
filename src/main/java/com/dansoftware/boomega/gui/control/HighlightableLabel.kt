package com.dansoftware.boomega.gui.control

import javafx.scene.Cursor
import javafx.scene.control.TextField

class HighlightableLabel(text: String? = null) : TextField(text) {
    init {
        this.styleClass.clear()
        this.styleClass.add("selectable-label")
        this.cursor = Cursor.TEXT
        this.style = "-fx-background-color: transparent;-fx-padding: 0;"
        this.prefColumnCount = 15
        this.styleClass.add("label")
        this.isEditable = false
    }
}