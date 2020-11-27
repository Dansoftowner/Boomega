package com.dansoftware.libraryapp.gui.pluginmanager.list;

import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;

public class PluginListModule extends WorkbenchModule {

//    private final PluginTable pluginTable;

    public PluginListModule() {
        super(I18N.getPluginManagerValues().getString("plugin.module.list"), MaterialDesignIcon.VIEW_LIST);
//        this.pluginTable = new PluginTable();
    }

    @Override
    public Node activate() {
//        return pluginTable;
        return null;
    }
}
