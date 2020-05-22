package com.dansoftware.dock.viewmode;

import com.dansoftware.dock.docknode.DockNode;
import com.dansoftware.dock.docksystem.DockSystem;

public class DockStrategy implements ViewModeStrategy {
    @Override
    public void show(DockNode dockNode) {
        DockSystem<?> dockSystem = dockNode.getDockSystem();
        dockSystem.dock(dockNode.getDockPosition(), dockNode, true);
    }

    @Override
    public void hide(DockNode dockNode) {
        DockSystem<?> dockSystem = dockNode.getDockSystem();
        dockSystem.hide(dockNode.getDockPosition(), dockNode);
    }
}
