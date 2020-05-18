package com.dansoftware.libraryapp.gui.dock.border;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

import static java.util.Objects.nonNull;

/**
 * A BorderButton is a Control that's located on a
 * {@link com.dansoftware.libraryapp.gui.dock.border.toolbar.BorderToolbar} located
 * on a {@link BorderEdge}.
 *
 * <p>
 * A BorderButton has multiple orientations represented by the enum: {@link ButtonOrientation}.
 *
 */
public class BorderButton extends Group {

    /**
     * Defines the css-style-class of this Node
     */
    private static final String STYLE_CLASS_NAME = "border-button";

    private final PseudoClass selectedPseudoClass = PseudoClass.getPseudoClass("selected");

    private ButtonOrientation buttonOrientation;
    private EventHandler<ActionEvent> onAction;
    private final ObjectProperty<Node> graphicProperty = new SimpleObjectProperty<>(this, "graphic");

    private final HBox hBox;
    private final Label label;

    public BorderButton() {
        this.label = new Label();
        this.hBox = new HBox(this.label);
        this.hBox.getStyleClass().add("h-box");

        this.getChildren().add(this.hBox);
        this.getStyleClass().add(STYLE_CLASS_NAME);

        //set the graphic when the graphicProperty changes
        this.graphicProperty.addListener((observable, oldGraphic, newGraphic) -> {
            this.hBox.getChildren().remove(oldGraphic);
            this.hBox.getChildren().add(0, newGraphic);
        });

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (nonNull(onAction)) onAction.handle(new ActionEvent());
            }
        });
    }

    public BorderButton(String text) {
        this();
        this.setText(text);
    }

    public BorderButton(Node graphic, String text) {
        this(text);
        this.setGraphic(graphic);
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

    public StringProperty textProperty() {
        return this.label.textProperty();
    }

    public void setText(String text) {
        this.textProperty().set(text);
    }

    public String getText() {
        return this.textProperty().get();
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction;
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        this.onAction = onAction;
    }

    public void setSelected(boolean value) {
        this.pseudoClassStateChanged(selectedPseudoClass, value);
    }

    public Node getGraphic() {
        return graphicProperty.get();
    }

    public void setGraphic(Node graphic) {
        this.graphicProperty.set(graphic);
    }

    /**
     * Represents the orientations of a {@link BorderButton}
     */
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
