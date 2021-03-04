package com.dansoftware.boomega.gui.dbcreator

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DatabaseCreatorWindow is a javaFX [Stage] that should be
 * used to display [DatabaseCreatorView] gui-objects.
 */
class DatabaseCreatorWindow(view: DatabaseCreatorView, owner: Window?) :
    BaseWindow("window.dbcreator.title", view, { view.context }) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 741.0
        height = 400.0
        centerOnScreen()
    }
}