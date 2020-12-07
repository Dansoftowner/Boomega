package com.dansoftware.libraryapp.gui.pluginmanager.list;

import com.dansoftware.libraryapp.gui.context.Context;
import com.dansoftware.libraryapp.gui.theme.Theme;
import com.dansoftware.libraryapp.gui.theme.Themeable;
import com.dansoftware.libraryapp.locale.I18N;
import com.dlsc.workbenchfx.model.WorkbenchModule;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * A {@link WorkbenchModule} for displaying a {@link PluginTable}.
 *
 * @author Daniel Gyorffy
 */
public class PluginListModule extends WorkbenchModule implements Themeable {

    private final PluginTable pluginTable;

    public PluginListModule(@NotNull Context context, @NotNull ObservableList<File> pluginFiles) {
        super(I18N.getPluginManagerValues().getString("plugin.module.list"), MaterialDesignIcon.VIEW_LIST);
        this.pluginTable = new PluginTable(context, pluginFiles);
        Theme.registerThemeable(this);
    }

    @Override
    public Node activate() {
        return pluginTable;
    }

    @Override
    public void handleThemeApply(Theme oldTheme, Theme newTheme) {
        oldTheme.applyBack(this.pluginTable);
        newTheme.apply(this.pluginTable);
    }
}
