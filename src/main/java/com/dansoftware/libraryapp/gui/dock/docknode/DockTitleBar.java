package com.dansoftware.libraryapp.gui.dock.docknode;

import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class DockTitleBar extends BorderPane {

    private static final String STYLE_CLASS_NAME = "dock-title-bar";

    private Label titleLabel;

    private final DockNode dockNode;

    public DockTitleBar(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockNode mustn't be null!");
        this.titleLabel = new Label();
        this.titleProperty().bind(this.dockNode.titleProperty());

        this.setRight(dockNode.getToolBar());
        this.setLeft(new StackPane(createLabel()));

        this.getStyleClass().add(STYLE_CLASS_NAME);
    }

    private Label createLabel() {
        var label = new Label();
        label.textProperty().bind(this.titleProperty());

        return label;
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public StringProperty titleProperty() {
        return titleLabel.textProperty();
    }

    public void setTitle(String name) {
        this.titleLabel.setText(name);
    }

    public DockNode getDockNode() {
        return dockNode;
    }
}
