package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.plugin.PluginDirectory
import javafx.stage.Window
import java.io.File

class PluginManagerActivity {

    fun show() = show(null)

    fun show(ownerWindow: Window?) {
        show(ownerWindow, PluginDirectory.getPluginFiles()?.asList())
    }

    fun show(ownerWindow: Window?, pluginFiles: List<File>?) {
        PluginManagerWindow(ownerWindow, PluginManager(pluginFiles ?: emptyList())).show()
    }
}