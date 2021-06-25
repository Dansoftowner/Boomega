package com.dansoftware.boomega.gui.preferences

import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.I18N
import javafx.stage.Modality
import javafx.stage.Window

class PreferencesWindow(view: PreferencesView, owner: Window?) :
    BaseWindow(I18N.getValue("window.preferences.title"), view, { null }) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        width = 800.0
        height = 500.0
    }
}