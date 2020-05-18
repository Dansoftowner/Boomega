package com.dansoftware.libraryapp.gui.dock.border;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class BorderButton extends Group {

    private ButtonOrientation buttonOrientation;

    private final ObjectProperty<Node> graphicProperty = new SimpleObjectProperty<>(this, "graphic");

    private final HBox hBox;
    private final Label label;

    public BorderButton() {
        this.label = new Label();
        this.hBox = new HBox(this.label);
        this.hBox.getStyleClass().add("h-box");

        this.getChildren().add(this.hBox);
        this.getStyleClass().add("border-button");
    }

    public BorderButton(String text) {
        this();
        this.label.setText(text);
    }

    public BorderButton(Node graphic, String text) {
        this(text);
        this.hBox.getChildren().add(0, graphic);
    }

    public void setButtonOrientation(ButtonOrientation buttonOrientation) {
        this.buttonOrientation = buttonOrientation;
        this.buttonOrientation.borderButtonConfigurator.accept(this);
    }

    public ButtonOrientation getButtonOrientation() {
        return buttonOrientation;
    }

    public ObjectProperty<Node> graphicProperty() {
        return this.graphicProperty;
    }

    public Node getGraphic() {
        return graphicProperty.get();
    }

    public void setGraphic(Node graphic) {
        this.graphicProperty.set(graphic);
    }

    public enum ButtonOrientation {
        LEFT_TOP(borderButton -> {
            borderButton.hBox.setRotate(-90);
            borderButton.hBox.setNodeOrientation(NodeOrientation.INHERIT);
        }),
        LEFT_BOTTOM(borderButton -> {
            borderButton.hBox.setRotate(90);
            borderButton.hBox.setNodeOrientation(NodeOrientation.INHERIT);
        }),
        RIGHT_TOP(borderButton -> {
            borderButton.hBox.setRotate(90);
            borderButton.hBox.setNodeOrientation(NodeOrientation.INHERIT);
        }),
        RIGHT_BOTTOM(borderButton -> {
            borderButton.hBox.setRotate(-90);
            borderButton.hBox.setNodeOrientation(NodeOrientation.INHERIT);
        }),
        HORIZONTAL_LEFT(borderButton -> {
            borderButton.hBox.setRotate(0);
            borderButton.hBox.setNodeOrientation(NodeOrientation.INHERIT);
        }),
        HORIZONTAL_RIGHT(borderButton -> {
            borderButton.hBox.setRotate(0);
            borderButton.hBox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        });

        private final Consumer<BorderButton> borderButtonConfigurator;

        ButtonOrientation(Consumer<BorderButton> borderButtonConfigurator) {
            this.borderButtonConfigurator = borderButtonConfigurator;
        }
    }
}
