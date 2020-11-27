package com.dansoftware.libraryapp.gui.window.undecorate.control;

import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class IconArea extends StackPane {

    private static final String STYLE_CLASS = "windowIconArea";

    IconArea(@NotNull Stage stage) {
        getStyleClass().add(STYLE_CLASS);
        getChildren().add(new IconAreaImageView());
    }

    private static final class IconAreaImageView extends ImageView {
        IconAreaImageView() { getStyleClass().add("graphic"); }
    }
}
