package com.dansoftware.dock.position.strategy;

import com.dansoftware.dock.border.BorderButton;
import com.dansoftware.dock.border.DockFrame;
import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.SplitPaneSystem;

public interface PositioningStrategy {
    void posDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode);
    void unPosDockNode(SplitPaneSystem splitPaneSystem, DockNode dockNode);

    void posBorderButton(DockFrame frame, BorderButton borderButton);
    void unPosBorderButton(DockFrame frame, BorderButton borderButton);
}
