package com.dansoftware.boomega.gui.pluginmngr

import com.dansoftware.boomega.plugin.PluginDirectory
import javafx.stage.Window
import java.io.File

/**
 * For showing a [PluginManager] with a [PluginManagerWindow].
 *
 * @author Daniel Gyorffy
 */
class PluginManagerActivity {

    fun show() = show(null)

    fun show(ownerWindow: Window?) {
        show(ownerWindow, PluginDirectory.getPluginFiles()?.asList())
    }

    fun show(ownerWindow: Window?, pluginFiles: List<File>?) {
        PluginManagerWindow(ownerWindow, PluginManager(pluginFiles ?: emptyList())).show()
    }
}