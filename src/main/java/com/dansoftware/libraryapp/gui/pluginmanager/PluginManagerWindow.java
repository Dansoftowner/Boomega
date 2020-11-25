package com.dansoftware.libraryapp.gui.pluginmanager;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.stage.Modality;
import org.jetbrains.annotations.NotNull;

public class PluginManagerWindow extends LibraryAppStage {
    PluginManagerWindow(@NotNull PluginManager pluginManager) {
        super("window.pluginmanager.title", pluginManager);
        initModality(Modality.APPLICATION_MODAL);
    }
}
