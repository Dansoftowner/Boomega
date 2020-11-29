package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.plugin.PluginDirectory
import java.io.File

class PluginManagerActivity {
    fun show() {
        show(PluginDirectory.getPluginFiles()?.asList())
    }

    fun show(pluginFiles: List<File>?)  {
        PluginManagerWindow(PluginManager(pluginFiles ?: emptyList())).show()
    }
}