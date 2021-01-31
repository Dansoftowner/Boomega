package com.dansoftware.libraryapp.gui.window.undecorate.control;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class TitledPane extends BorderPane {

    private static final String STYLE_CLASS = "internalTitledPane";
    private final TitleBar titleBar;

    public TitledPane(@NotNull Stage stage, @NotNull Node center) {
        super(center);
        this.getStyleClass().add(STYLE_CLASS);
        this.titleBar = new TitleBar(stage);
        this.setTop(titleBar);
    }

    public TitleBar getTitleBar() {
        return titleBar;
    }
}
