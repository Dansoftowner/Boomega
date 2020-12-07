package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A [javafx.stage.Stage] for showing a [PluginManager]
 *
 * @author Daniel Gyorffy
 */
private class PluginManagerWindow(owner: Window?, view: PluginManager) :
    BaseWindow<PluginManager>("window.pluginmanager.title", view) {
    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        width = 750.0
        height = 550.0
    }
}