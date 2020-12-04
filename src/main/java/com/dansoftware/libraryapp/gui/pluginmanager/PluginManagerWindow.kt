package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.gui.window.LibraryAppStage
import javafx.stage.Modality
import javafx.stage.Window

class PluginManagerWindow(owner: Window?, view: PluginManager) :
    LibraryAppStage<PluginManager>("window.pluginmanager.title", view) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        width = 750.0
        height = 550.0
    }
}