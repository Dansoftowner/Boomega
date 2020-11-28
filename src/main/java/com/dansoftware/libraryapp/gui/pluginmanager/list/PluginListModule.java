package com.dansoftware.libraryapp.gui.pluginmanager.list;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class PluginListModule extends WorkbenchModule {

    private final PluginTable pluginTable;

    public PluginListModule(@NotNull Context context, @NotNull List<File> pluginFiles) {
        super(I18N.getPluginManagerValues().getString("plugin.module.list"), MaterialDesignIcon.VIEW_LIST);
        this.pluginTable = new PluginTable(context, pluginFiles);
    }

    @Override
    public Node activate() {
        return pluginTable;
    }
}
