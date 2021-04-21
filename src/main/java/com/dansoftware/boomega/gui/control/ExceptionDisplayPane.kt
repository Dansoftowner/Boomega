package com.dansoftware.boomega.gui.control

import com.dansoftware.boomega.util.adapter.ThrowableString
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.TitledPane
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.CodeArea

class ExceptionDisplayPane(exception: Exception?) : TitledPane() {
    init {
        content = buildCodeArea(exception)
        isAnimated = true
        isExpanded = false
    }

    private fun buildCodeArea(exception: Exception?): Node =
        CodeArea(ThrowableString(exception).toString()).run {
            padding = Insets(5.0)
            isEditable = false
            prefHeight = 200.0
            VirtualizedScrollPane(this)
        }
}