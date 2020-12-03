package com.dansoftware.libraryapp.gui.dbmanager

import com.dansoftware.libraryapp.gui.window.LibraryAppStage
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A DBManagerWindow is used for displaying a [DatabaseManagerView] in a window.
 */
class DatabaseManagerWindow(view: DatabaseManagerView, owner: Window?) :
    LibraryAppStage<DatabaseManagerView>("window.dbmanager.title", view) {
    init {
        initModality(Modality.APPLICATION_MODAL)
        initOwner(owner)
        width = 1000.0
        height = 430.0
        centerOnScreen()
    }
}