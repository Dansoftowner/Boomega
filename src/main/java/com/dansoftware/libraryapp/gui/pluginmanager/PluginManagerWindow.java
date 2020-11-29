package com.dansoftware.libraryapp.gui.pluginmanager;

import com.dansoftware.libraryapp.gui.window.LibraryAppStage;
import javafx.stage.Modality;
import org.jetbrains.annotations.NotNull;

public class PluginManagerWindow extends LibraryAppStage {
    PluginManagerWindow(@NotNull PluginManager pluginManager) {
        super("window.pluginmanager.title", pluginManager);
        initModality(Modality.APPLICATION_MODAL);
        setWidth(750);
        setHeight(550);
    }
}
