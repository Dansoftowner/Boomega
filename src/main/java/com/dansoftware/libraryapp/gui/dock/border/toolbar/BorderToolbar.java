package com.dansoftware.libraryapp.gui.dock.border.toolbar;

import com.dansoftware.libraryapp.gui.dock.border.BorderButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.ToolBar;

import java.util.function.Consumer;

/**
 * A BorderToolbar is a {@link ToolBar} usually located on a
 * {@link com.dansoftware.libraryapp.gui.dock.border.BorderEdge}
 * that displays {@link BorderButton} objects.
 *
 * <p>
 * A BorderToolbar has multiple orientations that are represented by the enum: {@link ToolbarOrientation}
 *
 *
 */
public class BorderToolbar extends ToolBar {

    private static final String STYLE_CLASS_NAME = "border-toolbar";

    private BooleanBinding emptyProperty;
    private ToolbarOrientation toolbarOrientation;

    public BorderToolbar() {
        this.getStyleClass().add(STYLE_CLASS_NAME);
        this.emptyProperty = Bindings.isEmpty(this.getItems());
    }

    public void removeBorderButton(BorderButton borderButton) {
        this.getItems().remove(borderButton);
    }

    public void addBorderButton(BorderButton borderButton) {
        this.getItems().add(borderButton);
        borderButton.setButtonOrientation(this.toolbarOrientation.btnOrientation);
    }

    public void setToolbarOrientation(ToolbarOrientation toolbarOrientation) {
        this.toolbarOrientation = toolbarOrientation;
        this.toolbarOrientation.toolbarConfigurator.accept(this);
    }

    public BooleanBinding emptyProperty() {
        return emptyProperty;
    }

    /**
     * Represents the orientations of a {@link BorderToolbar}
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
