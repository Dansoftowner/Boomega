package com.dansoftware.libraryapp.gui.dock.border;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import javafx.scene.layout.BorderPane;

import java.util.Objects;

public class DockBorder extends BorderPane {

    private BorderEdge top;
    private BorderEdge left;
    private BorderEdge right;
    private BorderEdge bottom;

    public void allocate(DockPosition pos, BorderButton borderButton) {
        Objects.requireNonNull(pos, "The pos mustn't be null");
        Objects.requireNonNull(borderButton, "The borderButton mustn't be null");

        switch (pos) {
            case LEFT_TOP:
                this.setLeft(createLeft());
                this.left.getToolBar0().addBorderButton(borderButton);
                break;
            case LEFT_BOTTOM:
                this.setLeft(createLeft());
                this.left.getToolBar1().addBorderButton(borderButton);
                break;
            case BOTTOM_LEFT:
                this.setBottom(createBottom());
                this.bottom.getToolBar0().addBorderButton(borderButton);
                break;
            case BOTTOM_RIGHT:
                this.setBottom(createBottom());
                this.bottom.getToolBar1().addBorderButton(borderButton);
                break;
            case RIGHT_TOP:
                this.setRight(createRight());
                this.right.getToolBar0().addBorderButton(borderButton);
                break;
            case RIGHT_BOTTOM:
                this.setRight(createRight());
                this.right.getToolBar1().addBorderButton(borderButton);
                break;
            case TOP_RIGHT:
                this.setTop(createTop());
                this.top.getToolBar1().addBorderButton(borderButton);
                break;
            case TOP_LEFT:
                this.setTop(createTop());
                this.top.getToolBar0().addBorderButton(borderButton);
                break;
        }
    }

    private BorderEdge createTop() {
        if (this.top == null) {
            this.top = new BorderEdge();
            this.top.setOrientation(BorderEdge.EdgeOrientation.HORIZONTAL);
            this.top.emptyProperty().addListener((observable, wasEmpty, isEmpty) -> {
                if (isEmpty) this.setTop(null);
            });
        }

        return this.top;
    }

    private BorderEdge createBottom() {
        if (this.bottom == null) {
            this.bottom = new BorderEdge();
            this.bottom.setOrientation(BorderEdge.EdgeOrientation.HORIZONTAL);
            this.bottom.emptyProperty().addListener((observable, wasEmpty, isEmpty) -> {
                if (isEmpty) this.setBottom(null);
            });
        }

        return this.bottom;
    }

    private BorderEdge createLeft() {
        if (this.left == null) {
            this.left = new BorderEdge();
            this.left.setOrientation(BorderEdge.EdgeOrientation.LEFT);
            this.left.emptyProperty().addListener((observable, wasEmpty, isEmpty) -> {
                if (isEmpty) this.setLeft(null);
            });
        }

        return this.left;
    }

    private BorderEdge createRight() {
        if (this.right == null) {
            this.right = new BorderEdge();
            this.right.setOrientation(BorderEdge.EdgeOrientation.RIGHT);
            this.right.emptyProperty().addListener((observable, wasEmpty, isEmpty) -> {
                if (isEmpty) this.setRight(null);
            });
        }

        return this.right;
    }

}
