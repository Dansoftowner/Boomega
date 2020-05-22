package com.dansoftware.dock.border;

import com.dansoftware.dock.border.toolbar.BorderToolbar;
import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Objects;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

/**
 * A BorderButton is a Control that's located on a
 * {@link BorderToolbar} located
 * on a {@link BorderEdge}.
 *
 * <p>
 * A BorderButton has multiple orientations represented by the enum: {@link ButtonOrientation}.
 *
 * @author Daniel Gyorffy
 */
public class BorderButton extends Group {

    private static final String STYLE_CLASS_NAME = "border-button";

    /**
     * The object that represents the css-pseudo class of the button when it's selected
     */
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    /**
     * The current orientation of the button
     */
    private ButtonOrientation buttonOrientation;

    /**
     * The event handler of the user-clicks
     */
    private EventHandler<ActionEvent> onAction;

    /**
     * The observableValue that represents the button's graphic
     */
    private final ObjectProperty<Node> graphic = new SimpleObjectProperty<>(this, "graphic");

    private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected");

    private final HBox hBox;
    private final Label label;

    /**
     * Creates a basic BorderButton that doesn't have any
     * text or graphic.
     */
    public BorderButton() {
        this.label = new Label();
        this.hBox = new HBox(this.label);
        this.hBox.getStyleClass().add("root-btn");

        this.getChildren().add(this.hBox);
        this.getStyleClass().add(STYLE_CLASS_NAME);

        //set the graphic when the graphicProperty changes
        this.graphic.addListener((observable, oldGraphic, newGraphic) -> {
            this.hBox.getChildren().remove(oldGraphic);
            this.hBox.getChildren().add(0, newGraphic);
        });

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (isNull(onAction)) return;

            if (event.getButton() == MouseButton.PRIMARY) {
                onAction.handle(new ActionEvent(event.getSource(), event.getTarget()));
            }
        });

        this.selectedProperty().addListener((observable, oldValue, newValue) -> {
            this.hBox.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue);
        });
    }

    /**
     * Creates a BorderButton that has a text initially.
     *
     * @param text the text to display on the button.
     */
    public BorderButton(String text) {
        this();
        this.setText(text);
    }

    /**
     * Creates a BorderButton that has a text and graphic initially.
     *
     * @param graphic the graphic to display on the button
     * @param text the text to display on the button
     */
    public BorderButton(Node graphic, String text) {
        this(text);
        this.setGraphic(graphic);
    }

    /**
     * Sets the orientation of the button.
     *
     * @param buttonOrientation the button orientation; mustn't be null
     * @throws NullPointerException if the buttonOrientation is null
     */
    public void setButtonOrientation(ButtonOrientation buttonOrientation) {
        Objects.requireNonNull(buttonOrientation, "The buttonOrientation mustn't be null");
        this.buttonOrientation = buttonOrientation;
        this.buttonOrientation.borderButtonConfigurator.accept(this);
    }

    public ButtonOrientation getButtonOrientation() {
        return buttonOrientation;
    }

    /**
     * Gives access to the observableValue that represents the button's graphic
     */
    public ObjectProperty<Node> graphicProperty() {
        return this.graphic;
    }

    public Node getGraphic() {
        return graphic.get();
    }

    public void setGraphic(Node graphic) {
        this.graphic.set(graphic);
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

    public void setContextMenu(ContextMenu contextMenu) {
        this.setOnContextMenuRequested(event ->
                contextMenu.show(this, event.getScreenX(), event.getScreenY()));
    }

    public BooleanProperty selectedProperty() {
        return this.selected;
    }

    /**
     * Sets the state of te <i>:selected</i> css pseudo-class
     * on this button.
     *
     * @param value should be <code>true</code> if the button should act as a selected button;
     *              <code>false</code> if the button should act as a normal button.
     * @see Node#pseudoClassStateChanged(PseudoClass, boolean)
     */
    public void setSelected(boolean value) {
        this.selected.set(value);
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
