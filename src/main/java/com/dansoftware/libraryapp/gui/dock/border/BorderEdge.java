package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.dock.border.toolbar.BorderToolbar;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

public class BorderEdge extends BorderPane {

    private EdgeOrientation orientation;

    private final BorderToolbar toolBar0;
    private final BorderToolbar toolBar1;

    private final BooleanBinding emptyProperty;

    private final PseudoClass verticalPseudoClass = PseudoClass.getPseudoClass("vertical");
    private final PseudoClass horizontalPseudoClass = PseudoClass.getPseudoClass("horizontal");

    public BorderEdge() {
        this.toolBar0 = new BorderToolbar();
        this.toolBar1 = new BorderToolbar();

        this.emptyProperty = Bindings.isEmpty(toolBar0.getChildrenUnmodifiable()).and(Bindings.isEmpty(toolBar1.getChildrenUnmodifiable()));
        this.getStyleClass().add("border-edge");
    }

    public void setOrientation(EdgeOrientation orientation) {
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
