package com.dansoftware.libraryapp.gui.dock.docknode;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseButton;

import java.util.Objects;

public class DockNodeToolBar extends ToolBar {

    private final DockNode dockNode;

    public DockNodeToolBar(DockNode dockNode) {
        this.dockNode = Objects.requireNonNull(dockNode, "The dockNode mustn't be null");
        this.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        this.createDefaultButtons();
    }

    public void addCustomButtons(ObservableList<Button> customButtons) {
        this.getItems().addAll(0, customButtons);
        this.getItems().add(customButtons.size(), new Separator());
    }

    private void createDefaultButtons() {
        Button optionsButton = new Button();
        optionsButton.getStyleClass().add("options-btn");
        optionsButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        optionsButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                this.dockNode.getMenu().show(optionsButton, event.getScreenX(), event.getScreenY());
        });

        this.getItems().add(optionsButton);

        Button closeButton = new Button();
        closeButton.getStyleClass().add("close-btn");
        closeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        closeButton.setOnAction(event -> dockNode.setShowing(false));

        this.getItems().add(closeButton);
    }
}
