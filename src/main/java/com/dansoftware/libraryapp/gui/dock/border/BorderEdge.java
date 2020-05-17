package com.dansoftware.libraryapp.gui.dock.border;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;

public class BorderEdge extends BorderPane {

    private BorderToolbar toolBar0;
    private BorderToolbar toolBar1;

    public BorderEdge() {
        this.toolBar0 = new BorderToolbar();
        this.toolBar1 = new BorderToolbar();
    }

    public void setOrientation(Orientation orientation) {
        this.toolBar0.setOrientation(orientation.toolBarOrientation);
        this.toolBar1.setOrientation(orientation.toolBarOrientation);

        switch (orientation) {
            case LEFT_AND_RIGHT:
                this.setLeft(toolBar0);
                this.setRight(toolBar1);
                break;
            case TOP_AND_BOTTOM:
                this.setTop(toolBar0);
                this.setBottom(toolBar1);
                break;
        }
    }

    public ToolBar getToolBar0() {
        return toolBar0;
    }

    public ToolBar getToolBar1() {
        return toolBar1;
    }

    enum Orientation {
        TOP_AND_BOTTOM(javafx.geometry.Orientation.VERTICAL), LEFT_AND_RIGHT(javafx.geometry.Orientation.HORIZONTAL);

        private javafx.geometry.Orientation toolBarOrientation;

        Orientation(javafx.geometry.Orientation toolBarOrientation) {
            this.toolBarOrientation = toolBarOrientation;
        }
    }
}
