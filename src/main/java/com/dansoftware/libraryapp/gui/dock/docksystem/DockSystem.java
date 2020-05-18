package com.dansoftware.libraryapp.gui.dock.docksystem;

import com.dansoftware.libraryapp.gui.dock.DockPosition;
import com.dansoftware.libraryapp.gui.dock.border.DockBorder;
import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class DockSystem<C extends Node> extends StackPane {

    private final ObservableList<DockNode> dockNodes = FXCollections.observableArrayList();
    private final ObjectProperty<C> center = new SimpleObjectProperty<>(this, "centerPane");
    private final DockBorder border;
    private final SplitPaneSystem splitPaneSystem;


    public DockSystem() {
        this.splitPaneSystem = new SplitPaneSystem();
        this.border = new DockBorder();
        this.border.setCenter(splitPaneSystem);

        this.getChildren().add(this.border);

        this.center.addListener((observable, oldCenter, newCenter) ->
                splitPaneSystem.setCenterNode(newCenter));
    }

    public void hide(DockPosition pos, DockNode dockNode) {
        Objects.requireNonNull(pos, "The pos mustn't be null");

        switch (pos) {

            case LEFT_TOP:
            case LEFT_BOTTOM:
                this.splitPaneSystem
                        .getLeftVertical()
                        .getItems()
                        .remove(dockNode);
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                this.splitPaneSystem
                        .getBottomHorizontal()
                        .getItems()
                        .remove(dockNode);
                break;
            case RIGHT_TOP:
            case RIGHT_BOTTOM:
                this.splitPaneSystem
                        .getRightVertical()
                        .getItems()
                        .remove(dockNode);
                break;
            case TOP_RIGHT:
            case TOP_LEFT:
                this.splitPaneSystem
                        .getTopHorizontal()
                        .getItems()
                        .remove(dockNode);
                break;
        }
    }

    public void dock(DockPosition pos, DockNode dockNode) {
        if (pos == null) pos = DockPosition.TOP_LEFT;

        this.border.allocate(pos, dockNode.getBorderButton());

        dockNode.setDockSystem(this);
        dockNode.setDockPosition(pos);

        switch (pos) {

            case LEFT_TOP:
                this.splitPaneSystem
                        .getLeftVertical()
                        .getItems()
                        .add(0, dockNode);
                break;
            case LEFT_BOTTOM:
                this.splitPaneSystem
                        .getLeftVertical()
                        .getItems()
                        .add(dockNode);
                break;
            case BOTTOM_LEFT:
                this.splitPaneSystem
                        .getBottomHorizontal()
                        .getItems()
                        .add(0, dockNode);
                break;
            case BOTTOM_RIGHT:
                this.splitPaneSystem
                        .getBottomHorizontal()
                        .getItems()
                        .add(dockNode);
                break;
            case RIGHT_TOP:
                this.splitPaneSystem
                        .getRightVertical()
                        .getItems()
                        .add(0, dockNode);
                break;
            case RIGHT_BOTTOM:
                this.splitPaneSystem
                        .getRightVertical()
                        .getItems()
                        .add(dockNode);
                break;
            case TOP_RIGHT:
                this.splitPaneSystem
                        .getTopHorizontal()
                        .getItems()
                        .add(dockNode);
                break;
            case TOP_LEFT:
                this.splitPaneSystem
                        .getTopHorizontal()
                        .getItems()
                        .add(0, dockNode);
                break;
        }

        this.dockNodes.add(dockNode);
    }

    public ObservableList<DockNode> getDockNodes() {
        return dockNodes;
    }

    public Node getCenter() {
        return center.get();
    }

    public ObjectProperty<C> centerProperty() {
        return center;
    }

    public void setCenter(C center) {
        this.center.set(center);
    }
}
