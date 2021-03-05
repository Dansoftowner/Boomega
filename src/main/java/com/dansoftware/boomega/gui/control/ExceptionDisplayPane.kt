package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.util.adapter.ThrowableString
import javafx.scene.control.TextArea
import javafx.scene.control.TitledPane

class ExceptionDisplayPane(exception: Exception?) : TitledPane() {
    init {
        content = TextArea(ThrowableString(exception).toString())
        isAnimated = true
        isExpanded = false
    }
}