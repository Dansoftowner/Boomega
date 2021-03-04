package com.dansoftware.boomega.gui.dbmanager

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DBManagerWindow is used for displaying a [DatabaseManagerView] in a window.
 */
class DatabaseManagerWindow(view: DatabaseManagerView, owner: Window?) :
    BaseWindow("window.dbmanager.title", view, { view.context }) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 1000.0
        height = 430.0
        centerOnScreen()
    }
}