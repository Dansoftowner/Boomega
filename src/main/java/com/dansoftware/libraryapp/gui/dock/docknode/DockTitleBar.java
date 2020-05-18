package com.dansoftware.libraryapp.gui.dock.docknode;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class DockTitleBar extends BorderPane {

    private static final String STYLE_CLASS_NAME = "dock-title-bar";

    private Label titleLabel;
    private ContextMenu menu;
    private ToolBar toolBar;

    private final DockNode dockNode;

    public DockTitleBar(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockNode mustn't be null!");
        this.titleProperty().bind(this.dockNode.titleProperty());

        //this.setRight(createToolbar());
        this.setLeft(createLabel());

        this.getStyleClass().add(STYLE_CLASS_NAME);
    }

    private Label createLabel() {
        Label label = new Label();
        label.textProperty().bind(this.titleProperty());

        return label;
    }

    private Button createCloseButton() {
        Button btn = new Button();
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.setGraphic(null);
        btn.setTooltip(
                new Tooltip(

                )
        );
        btn.setOnAction(event -> dockNode.hide());

        return null;
    }

    private Button createOptionsButton() {
        return null;
    }

    public String getTitle() {
        return titleLabel.textProperty().get();
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
