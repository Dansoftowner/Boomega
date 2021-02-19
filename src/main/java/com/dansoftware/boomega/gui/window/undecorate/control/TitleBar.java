package com.dansoftware.boomega.gui.window.undecorate.control;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class TitleBar extends BorderPane {

    private static final String STYLE_CLASS = "windowTitleBar";

    private final ControlBox controlBox;
    private final TitleLabel titleLabel;
    private final IconArea iconArea;

    public TitleBar(@NotNull Stage stage) {
        this.getStyleClass().add(STYLE_CLASS);
        this.controlBox = new ControlBox(stage);
        this.iconArea = new IconArea(stage);
        this.titleLabel = new TitleLabel(stage);
        this.setLeft(iconArea);
        this.setRight(controlBox);
        this.setCenter(titleLabel);
    }

    public TitleLabel getTitleLabel() {
        return titleLabel;
    }

    public ControlBox getControlBox() {
        return controlBox;
    }

    public IconArea getIconArea() {
        return iconArea;
    }
}
