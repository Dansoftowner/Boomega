package com.dansoftware.libraryapp.gui.pluginmanager;

import com.dansoftware.libraryapp.gui.pluginmanager.adder.PluginAdderModule;
import com.dansoftware.libraryapp.gui.pluginmanager.list.PluginListModule;
import com.dlsc.workbenchfx.Workbench;

public class PluginManager extends Workbench {

    PluginManager() {
        getModules().addAll(new PluginListModule(), new PluginAdderModule());
    }
}
