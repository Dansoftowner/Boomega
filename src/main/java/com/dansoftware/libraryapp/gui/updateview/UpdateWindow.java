package com.dansoftware.libraryapp.gui.updateview;

import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class UpdateWindow extends Stage {

    public UpdateWindow(@NotNull UpdateView view) {
        this.initModality(Modality.APPLICATION_MODAL);

    }
}
