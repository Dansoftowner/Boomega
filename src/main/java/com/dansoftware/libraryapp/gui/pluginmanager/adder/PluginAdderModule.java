package com.dansoftware.libraryapp.gui.pluginmanager.adder;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PluginAdderModule extends WorkbenchModule {

    private final PluginAdderPane pluginAdderPane;

    public PluginAdderModule(@NotNull Context context, @NotNull ObservableList<File> pluginList) {
        super(I18N.getPluginManagerValue("plugin.module.adder"), MaterialDesignIcon.PLUS_BOX);
        this.pluginAdderPane = new PluginAdderPane(context, pluginList);
    }

    @Override
    public Node activate() {
        return pluginAdderPane;
    }
}
