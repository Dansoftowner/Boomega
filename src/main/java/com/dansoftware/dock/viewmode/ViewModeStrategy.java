package com.dansoftware.dock.viewmode;

import com.dansoftware.dock.docknode.DockNode;

public interface ViewModeStrategy {
    void show(DockNode dockNode);
    void hide(DockNode dockNode);
}
