package com.dansoftware.libraryapp.gui.firsttime.imp;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.stage.Modality;
import org.jetbrains.annotations.NotNull;

public class ConfigurationImportWindow extends LibraryAppStage {
    public ConfigurationImportWindow(@NotNull ConfigurationImportView view) {
        super("window.config.import.title", view);
        initModality(Modality.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(false);
        setWidth(500);
        setHeight(250);
    }
}
