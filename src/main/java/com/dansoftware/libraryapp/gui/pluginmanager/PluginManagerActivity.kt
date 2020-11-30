package com.dansoftware.libraryapp.gui.pluginmanager

import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.plugin.PluginDirectory
import java.io.File

class PluginManagerActivity {
    fun show(context: Context?) {
        show(context, PluginDirectory.getPluginFiles()?.asList())
    }

    fun show(context: Context?, pluginFiles: List<File>?)  {
        PluginManagerWindow(context, PluginManager(pluginFiles ?: emptyList())).show()
    }
}