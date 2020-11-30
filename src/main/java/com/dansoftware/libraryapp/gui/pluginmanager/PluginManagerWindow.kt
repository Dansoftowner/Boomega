package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.window.LibraryAppStage
import javafx.stage.Modality

class PluginManagerWindow(context: Context?, pluginManager: PluginManager) : LibraryAppStage("window.pluginmanager.title", pluginManager) {
    init {
        if (context != null) initOwner(context.contextWindow)
        initModality(Modality.APPLICATION_MODAL)
        width = 750.0
        height = 550.0
    }
}