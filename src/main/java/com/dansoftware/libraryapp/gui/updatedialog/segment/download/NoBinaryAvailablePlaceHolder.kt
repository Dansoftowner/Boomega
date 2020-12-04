package com.dansoftware.libraryapp.gui.updatedialog.segment.download

import com.dansoftware.libraryapp.locale.I18N
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.text.Font

private class NoBinaryAvailablePlaceHolder : StackPane() {
    init {
        styleClass.add("no-binary-available-place-holder")
        buildUI()
    }

    private fun buildUI() {
        children.add(CenterLabel(I18N.getUpdateDialogValue("segment.download.no.binary.available")))
    }

    private class CenterLabel(text: String) : Label(text) {
        init {
            font = Font.font(15.0)
        }
    }
}