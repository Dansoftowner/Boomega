package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import com.dansoftware.libraryapp.main.Preloader;
import javafx.stage.Modality;
import org.jetbrains.annotations.NotNull;

public class FirstTimeDialogWindow extends LibraryAppStage {

    public FirstTimeDialogWindow(@NotNull FirstTimeDialog firstTimeDialog) {
        super("window.firsttime.title", firstTimeDialog);
        initOwner(Preloader.getBackingStage());
        initModality(Modality.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(true);
    }
}
