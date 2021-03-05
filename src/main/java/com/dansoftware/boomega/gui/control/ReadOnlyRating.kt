package com.dansoftware.boomega.gui.control

import javafx.scene.input.MouseEvent
import org.controlsfx.control.Rating

class ReadOnlyRating(max: Int, value: Int) : Rating(max, value) {
    init {
        this.addEventFilter(MouseEvent.MOUSE_CLICKED) { it.consume() }
    }
}