package com.dansoftware.libraryapp.gui.dock.docksystem;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class SplitPaneSystem extends SplitPane {

    private SplitPane topHorizontal;
    private SplitPane bottomHorizontal;
    private SplitPane centerHorizontal;
    private SplitPane leftVertical;
    private SplitPane rightVertical;

    public SplitPaneSystem() {
        this.setOrientation(Orientation.VERTICAL);
        this.initSplitPanes();
        this.buildSplitPaneSystem();
    }

    private void buildSplitPaneSystem() {
        //building the whole splitpane (this)
        this.getItems().addAll(topHorizontal, centerHorizontal, bottomHorizontal);

        //building the center-horizontal splitpane
        this.centerHorizontal.getItems().addAll(leftVertical, rightVertical);
    }

    private void initSplitPanes() {
        this.topHorizontal = new SplitPane();
        this.topHorizontal.setOrientation(Orientation.HORIZONTAL);

        this.bottomHorizontal = new SplitPane();
        this.bottomHorizontal.setOrientation(Orientation.HORIZONTAL);

        this.centerHorizontal = new SplitPane();
        this.centerHorizontal.setOrientation(Orientation.HORIZONTAL);

        this.leftVertical = new SplitPane();
        this.leftVertical.setOrientation(Orientation.VERTICAL);

        this.rightVertical = new SplitPane();
        this.rightVertical.setOrientation(Orientation.VERTICAL);
    }

    public void setCenterNode(Node centerNode) {
        this.centerHorizontal.getItems().remove(getCenterNode());
        this.centerHorizontal.getItems().add(1, centerNode);
    }

    public Node getCenterNode() {
        if (this.centerHorizontal.getItems().size() < 3) return null;
        return this.centerHorizontal.getItems().get(1);
    }

    public SplitPane getTopHorizontal() {
        return topHorizontal;
    }

    public SplitPane getBottomHorizontal() {
        return bottomHorizontal;
    }

    public SplitPane getLeftVertical() {
        return leftVertical;
    }

    public SplitPane getRightVertical() {
        return rightVertical;
    }
}
