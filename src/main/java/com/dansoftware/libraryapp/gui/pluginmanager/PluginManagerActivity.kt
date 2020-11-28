package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.plugin.PluginDirectory
import java.io.File

class PluginManagerActivity {
    fun show(pluginFiles: List<File>? = PluginDirectory.getPluginFiles()?.asList())  {
        PluginManagerWindow(PluginManager(pluginFiles ?: emptyList())).show()
    }
}