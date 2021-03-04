package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

class PreferencesWindow(view: PreferencesView, owner: Window?) :
    BaseWindow("window.preferences.title", view, { null }) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        isResizable = false
        width = 800.0
        height = 500.0
    }
}