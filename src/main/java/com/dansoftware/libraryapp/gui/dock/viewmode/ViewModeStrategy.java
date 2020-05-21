package com.dansoftware.libraryapp.gui.dock.viewmode;

import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;

public interface ViewModeStrategy {
    void show(DockNode dockNode);
    void hide(DockNode dockNode);
}
