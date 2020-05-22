package com.dansoftware.dock.border;

import com.dansoftware.dock.border.toolbar.BorderToolbar;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

/**
 * A BorderEdge is a narrow area on the edges of a {@link DockFrame}.
 *
 * <p>
 * It has two {@link BorderToolbar} elements on the left and right side
 * <b>OR</b> on the top and the bottom side (it depends on the
 * orientation)
 *
 * <p>
 * A BorderEdge has multiple orientations that are represented by the enum: {@link EdgeOrientation}.
 */
public class BorderEdge extends BorderPane {

    private static final String STYLE_CLASS_NAME = "border-edge";

    /**
     * The current orientation
     */
    private EdgeOrientation orientation;

    private final BorderToolbar toolBar0;
    private final BorderToolbar toolBar1;

    /**
     * The observableValue that represents that the edge has buttons
     * or not.
     */
    private final BooleanBinding emptyProperty;

    /**
     * The css pseudo-class that defines that the edge's orientation is vertical
     */
    private final PseudoClass verticalPseudoClass = PseudoClass.getPseudoClass("vertical");

    /**
     * The css pseudo-class that defines that the edge's orientation is horizontal
     */
    private final PseudoClass horizontalPseudoClass = PseudoClass.getPseudoClass("horizontal");

    /**
     * Creates a basic BorderEdge
     */
    public BorderEdge() {
        this.toolBar0 = new BorderToolbar();
        this.toolBar1 = new BorderToolbar();

        this.emptyProperty = this.toolBar0.emptyProperty()
                .and(toolBar1.emptyProperty());
        this.getStyleClass().add(STYLE_CLASS_NAME);
    }

    /**
     * Creates a BorderEdge with an initial orientation
     *
     * @param orientation the orientation to set to; mustn't be null
     * @throws NullPointerException if the orientation is null
     */
    public BorderEdge(EdgeOrientation orientation) {
        this();
        this.setOrientation(orientation);
    }

    /**
     * Sets the orientation of the border-edge.
     *
     * @param orientation the new orientation of the edge; mustn't be null
     * @throws NullPointerException if the orientation is null
     */
    public void setOrientation(EdgeOrientation orientation) {
        Objects.requireNonNull(orientation, "The orientation mustn't be null");

        this.orientation = orientation;
        this.toolBar0.setToolbarOrientation(orientation.toolbar0Orientation);
        this.toolBar1.setToolbarOrientation(orientation.toolbar1Orientation);

        switch (orientation) {
            case HORIZONTAL:
                this.setLeft(this.toolBar0);
                this.setRight(this.toolBar1);
                this.pseudoClassStateChanged(horizontalPseudoClass, true);
                this.pseudoClassStateChanged(verticalPseudoClass, false);
                break;
            case LEFT:
            case RIGHT:
                this.setTop(this.toolBar0);
                this.setBottom(this.toolBar1);
                this.pseudoClassStateChanged(horizontalPseudoClass, false);
                this.pseudoClassStateChanged(verticalPseudoClass, true);
                break;
        }
    }

    public BooleanBinding emptyProperty() {
        return emptyProperty;
    }

    public EdgeOrientation getOrientation() {
        return orientation;
    }

    public BorderToolbar getToolBar0() {
        return toolBar0;
    }

    public BorderToolbar getToolBar1() {
        return toolBar1;
    }

    /**
     * Represents the orientations of a {@link BorderEdge}
     */
    enum EdgeOrientation {
        LEFT(BorderToolbar.ToolbarOrientation.LEFT_TOP, BorderToolbar.ToolbarOrientation.LEFT_BOTTOM),
        RIGHT(BorderToolbar.ToolbarOrientation.RIGHT_TOP, BorderToolbar.ToolbarOrientation.RIGHT_BOTTOM),
        HORIZONTAL(BorderToolbar.ToolbarOrientation.HORIZONTAL_LEFT, BorderToolbar.ToolbarOrientation.HORIZONTAL_RIGHT);

        private final BorderToolbar.ToolbarOrientation toolbar0Orientation;
        private final BorderToolbar.ToolbarOrientation toolbar1Orientation;

        EdgeOrientation(BorderToolbar.ToolbarOrientation toolbar0Orientation, BorderToolbar.ToolbarOrientation toolbar1Orientation) {
            this.toolbar0Orientation = toolbar0Orientation;
            this.toolbar1Orientation = toolbar1Orientation;
        }
    }
}
