package com.dansoftware.boomega.gui.pluginmngr

import com.dansoftware.boomega.gui.window.BaseWindow
import javafx.stage.Modality
import javafx.stage.Window

/**
 * A [javafx.stage.Stage] for showing a [PluginManager]
 *
 * @author Daniel Gyorffy
 */
internal class PluginManagerWindow(owner: Window?, view: PluginManager) :
    BaseWindow("window.pluginmanager.title", view, { view.context }) {

    init {
        initOwner(owner)
        initModality(Modality.APPLICATION_MODAL)
        width = 750.0
        height = 550.0
    }
}