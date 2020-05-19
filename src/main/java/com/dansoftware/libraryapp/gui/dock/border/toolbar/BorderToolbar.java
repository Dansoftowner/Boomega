package com.dansoftware.libraryapp.gui.dock.border.toolbar;

import com.dansoftware.libraryapp.gui.dock.border.BorderButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ToolBar;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A BorderToolbar is a {@link ToolBar} usually located on a
 * {@link com.dansoftware.libraryapp.gui.dock.border.BorderEdge}
 * that displays {@link BorderButton} GUI objects.
 *
 * <p>
 * A BorderToolbar has multiple orientations that are represented by the enum: {@link ToolbarOrientation}
 *
 * @author Daniel Gyorffy
 */
public class BorderToolbar extends ToolBar {

    private static final String STYLE_CLASS_NAME = "border-toolbar";

    /**
     * An observable value that represents that the toolbar
     * has visible buttons or not.
     */
    private final BooleanBinding emptyProperty;

    /**
     * The current orientation
     */
    private ToolbarOrientation toolbarOrientation;

    /**
     * Creates a normal BorderToolbar
     */
    public BorderToolbar() {
        this.getStyleClass().add(STYLE_CLASS_NAME);
        this.emptyProperty = Bindings.isEmpty(this.getItems());
    }

    /**
     * Removes the given {@link BorderButton} from the
     * toolbar.
     *
     * @param borderButton the button to remove;
     */
    public void removeBorderButton(BorderButton borderButton) {
        this.getItems().remove(borderButton);
    }

    /**
     * Adds a {@link BorderButton} to the toolbar.
     *
     * <p>
     * Automatically sets the button's orientation to the right
     * {@link BorderButton.ButtonOrientation} depends on what is
     * the orientation of the toolbar.
     *
     * @param borderButton the borderButton to add; mustn't be null
     * @throws NullPointerException if the borderButton is null
     */
    public void addBorderButton(BorderButton borderButton) {
        Objects.requireNonNull(borderButton, "The borderButton mustn't be null!");
        this.getItems().add(borderButton);
        borderButton.setButtonOrientation(this.toolbarOrientation.btnOrientation);
    }

    /**
     * Sets the toolbar's orientation.
     *
     * @param toolbarOrientation the orientation
     */
    public void setToolbarOrientation(ToolbarOrientation toolbarOrientation) {
        this.toolbarOrientation = toolbarOrientation;
        this.toolbarOrientation.toolbarConfigurator.accept(this);
    }

    /**
     * Returns an observable-value that represents that
     * the toolbar has visible buttons or not.
     *
     * @return the observable value.
     */
    public BooleanBinding emptyProperty() {
        return emptyProperty;
    }

    /**
     * Represents the orientations of a {@link BorderToolbar}.
     *
     * <p>
     * Every ToolbarOrientation has two properties: a toolbarConfigurator
     * with the type {@link Consumer} that actually sets the toolbar's orientation;
     * and a btnOrientation with the type {@link BorderButton.ButtonOrientation}
     * that defines the orientation of the buttons on the toolbar.
     *
     */
    public enum ToolbarOrientation {
        LEFT_TOP(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.VERTICAL);
            toolBar.setNodeOrientation(NodeOrientation.INHERIT);
            toolBar.setRotate(0);
        }, BorderButton.ButtonOrientation.LEFT_TOP),

        LEFT_BOTTOM(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.VERTICAL);
            toolBar.setNodeOrientation(NodeOrientation.INHERIT);
            toolBar.setRotate(180);
        }, BorderButton.ButtonOrientation.LEFT_BOTTOM),

        RIGHT_TOP(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.VERTICAL);
            toolBar.setNodeOrientation(NodeOrientation.INHERIT);
            toolBar.setRotate(0);
        }, BorderButton.ButtonOrientation.RIGHT_TOP),

        RIGHT_BOTTOM(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.VERTICAL);
            toolBar.setNodeOrientation(NodeOrientation.INHERIT);
            toolBar.setRotate(180);
        }, BorderButton.ButtonOrientation.RIGHT_BOTTOM),

        HORIZONTAL_LEFT(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
            toolBar.setNodeOrientation(NodeOrientation.INHERIT);
            toolBar.setRotate(0);
        }, BorderButton.ButtonOrientation.HORIZONTAL_LEFT),

        HORIZONTAL_RIGHT(toolBar -> {
            toolBar.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
            toolBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            toolBar.setRotate(0);
        }, BorderButton.ButtonOrientation.HORIZONTAL_RIGHT);

        private final Consumer<BorderToolbar> toolbarConfigurator;
        private final BorderButton.ButtonOrientation btnOrientation;

        ToolbarOrientation(Consumer<BorderToolbar> toolbarConfigurator, BorderButton.ButtonOrientation btnOrientation) {
            this.toolbarConfigurator = toolbarConfigurator;
            this.btnOrientation = btnOrientation;
        }
    }
}
