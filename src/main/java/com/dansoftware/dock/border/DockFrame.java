package com.dansoftware.dock.border;

import com.dansoftware.dock.docksystem.SplitPaneSystem;
import com.dansoftware.dock.position.DockPosition;
import com.dansoftware.dock.util.ListObserver;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

/**
 * A DockFrame is a frame that surrounds a {@link SplitPaneSystem}.
 *
 *
 */
public class DockFrame extends BorderPane {

    private final ListObserver observer =
            new ListObserver(4);

    private BorderEdge top;
    private BorderEdge left;
    private BorderEdge right;
    private BorderEdge bottom;

    public DockFrame() {
        this.initBorderEdges();
        this.addListeners();
    }

    private void initBorderEdges() {
        this.top = new BorderEdge();
        this.top.setOrientation(BorderEdge.EdgeOrientation.HORIZONTAL);

        this.bottom = new BorderEdge();
        this.bottom.setOrientation(BorderEdge.EdgeOrientation.HORIZONTAL);

        this.left = new BorderEdge();
        this.left.setOrientation(BorderEdge.EdgeOrientation.LEFT);

        this.right = new BorderEdge();
        this.right.setOrientation(BorderEdge.EdgeOrientation.RIGHT);
    }

    private void addListeners() {
        top.emptyProperty().addListener((observable, wasEmpty, empty) -> {
            if (empty) this.setTop(null);
            else this.setTop(top);
        });

       bottom.emptyProperty().addListener((observable, wasEmpty, empty) -> {
            if (empty) this.setBottom(null);
            else this.setBottom(bottom);
        });

        left.emptyProperty().addListener((observable, wasEmpty, empty) -> {
            if (empty) this.setLeft(null);
            else this.setLeft(left);
        });

        right.emptyProperty().addListener((observable, wasEmpty, empty) -> {
            if (empty) this.setRight(null);
            else this.setRight(right);
        });

    }

    /**
     * Adds a {@link BorderButton} to the specified position.
     *
     * @param pos the position of the button; mustn't be null
     * @param borderButton the actual button to locate on the frame; mustn't be null
     * @throws NullPointerException if the pos or the borderButton is null
     */
    public void allocate(DockPosition pos, BorderButton borderButton) {
        Objects.requireNonNull(pos, "The pos mustn't be null");
        Objects.requireNonNull(borderButton, "The borderButton mustn't be null");

        pos.getPosStrategy().posBorderButton(this, borderButton);
    }

    public void deAllocate(DockPosition pos, BorderButton borderButton) {
        pos.getPosStrategy().unPosBorderButton(this, borderButton);
    }

    public BorderEdge getTopEdge() {
        return top;
    }

    public BorderEdge getLeftEdge() {
        return left;
    }

    public BorderEdge getRightEdge() {
        return right;
    }

    public BorderEdge getBottomEdge() {
        return bottom;
    }
}
