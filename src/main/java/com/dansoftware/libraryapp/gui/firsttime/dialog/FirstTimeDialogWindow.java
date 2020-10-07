package com.dansoftware.libraryapp.gui.firsttime.dialog;

import com.dansoftware.libraryapp.gui.util.LibraryAppStage;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;

public class FirstTimeDialogWindow extends LibraryAppStage{

    public FirstTimeDialogWindow(@NotNull FirstTimeDialog firstTimeDialog) {
        super("window.firsttime.title", firstTimeDialog);
        initOwner(Window.getWindows().get(0));
        initModality(Modality.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(false);
    }
}
