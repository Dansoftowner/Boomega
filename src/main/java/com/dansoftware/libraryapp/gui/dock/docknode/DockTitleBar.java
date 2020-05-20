package com.dansoftware.libraryapp.gui.dock.docknode;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class DockTitleBar extends BorderPane {

    private static final String STYLE_CLASS_NAME = "dock-title-bar";

    private final DockNode dockNode;

    public DockTitleBar(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockNode mustn't be null!");

        this.setRight(new DockNodeToolBar(this.dockNode));
        this.setLeft(new StackPane(createLabel()));

        this.getStyleClass().add(STYLE_CLASS_NAME);
    }

    private Label createLabel() {
        var label = new Label();
        label.textProperty().bind(this.dockNode.titleProperty());

        return label;
    }

    public DockNode getDockNode() {
        return dockNode;
    }

    public void applyFloating() {

    }

    public void disableFloating() {

    }
}
