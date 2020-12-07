package com.dansoftware.libraryapp.gui.dbcreator

import com.dansoftware.libraryapp.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DatabaseCreatorWindow is a javaFX [Stage] that should be
 * used to display [DatabaseCreatorView] gui-objects.
 */
class DatabaseCreatorWindow(view: DatabaseCreatorView, owner: Window?) :
    BaseWindow<DatabaseCreatorView>("window.dbcreator.title", view) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 741.0
        height = 400.0
        centerOnScreen()
    }
}