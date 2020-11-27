package com.dansoftware.libraryapp.gui.window.undecorate.control;

import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class TitleLabel extends Label {

    private static final String STYLE_CLASS = "windowTitleLabel";

    TitleLabel(@NotNull Stage stage) {
        getStyleClass().add(STYLE_CLASS);
        this.textProperty().bind(stage.titleProperty());
    }

}
