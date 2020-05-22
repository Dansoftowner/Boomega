package com.dansoftware.dock.position.strategy;

import com.dansoftware.dock.border.BorderButton;
import com.dansoftware.dock.border.DockFrame;
import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.SplitPaneSystem;

public class BottomLeftStrategy implements PositioningStrategy {
    @Override
    public void posDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode) {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .add(0, dockNode);
    }

    @Override
    public void unPosDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode) {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .remove(dockNode);
    }

    @Override
    public void posBorderButton(DockFrame frame, BorderButton borderButton) {
        frame.getBottomEdge().getToolBar0().addBorderButton(borderButton);
    }

    @Override
    public void unPosBorderButton(DockFrame frame, BorderButton borderButton) {
        frame.getBottomEdge().getToolBar0().removeBorderButton(borderButton);
    }
}
