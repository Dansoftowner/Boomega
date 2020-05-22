package com.dansoftware.dock.position.strategy;

import com.dansoftware.dock.border.BorderButton;
import com.dansoftware.dock.border.DockFrame;
import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.SplitPaneSystem;

public class LeftBottomStrategy implements PositioningStrategy {
    @Override
    public void posDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode) {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .add(dockNode);
    }

    @Override
    public void unPosDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode) {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .remove(dockNode);
    }

    @Override
    public void posBorderButton(DockFrame frame, BorderButton borderButton) {
        frame.getLeftEdge().getToolBar1().addBorderButton(borderButton);
    }

    @Override
    public void unPosBorderButton(DockFrame frame, BorderButton borderButton) {
        //frame.getLeftEdge().getToolBar0().removeBorderButton(borderButton);
        frame.getLeftEdge().getToolBar1().removeBorderButton(borderButton);
    }
}
