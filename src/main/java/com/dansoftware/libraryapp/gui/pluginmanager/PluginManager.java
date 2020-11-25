package com.dansoftware.libraryapp.gui.pluginmanager;

import com.dlsc.workbenchfx.Workbench;

public class PluginManager extends Workbench {

    PluginManager() {
        getModules().addAll(new PluginListModule(), new PluginAdderModule());
    }
}
