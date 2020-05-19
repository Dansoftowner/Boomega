package com.dansoftware.libraryapp.gui.dock;

import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.dock.docksystem.SplitPaneSystem;
import com.dansoftware.libraryapp.util.function.DualConsumer;

import java.util.function.Consumer;

public enum DockPosition {
    LEFT_TOP("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .add(0, dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .remove(dockNode);
    }),

    LEFT_BOTTOM("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .add(dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getLeftVertical()
                .getItems()
                .remove(dockNode);
    }),

    BOTTOM_LEFT("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .add(0, dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .remove(dockNode);
    }),

    BOTTOM_RIGHT("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .add(dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getBottomHorizontal()
                .getItems()
                .remove(dockNode);
    }),

    RIGHT_TOP("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getRightVertical()
                .getItems()
                .add(0, dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getRightVertical()
                .getItems()
                .remove(dockNode);
    }),

    RIGHT_BOTTOM("",(splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getRightVertical()
                .getItems()
                .add(dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getRightVertical()
                .getItems()
                .remove(dockNode);
    }),

    TOP_RIGHT("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getTopHorizontal()
                .getItems()
                .add(dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getTopHorizontal()
                .getItems()
                .remove(dockNode);
    }),

    TOP_LEFT("", (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getTopHorizontal()
                .getItems()
                .add(0, dockNode);
    }, (splitPaneSystem, dockNode) -> {
        splitPaneSystem
                .getTopHorizontal()
                .getItems()
                .remove(dockNode);
    });

    private final String localeKey;
    private final DualConsumer<SplitPaneSystem, DockNode> adder;
    private final DualConsumer<SplitPaneSystem, DockNode> remover;

    DockPosition(String localeKey, DualConsumer<SplitPaneSystem, DockNode> adder, DualConsumer<SplitPaneSystem, DockNode> remover) {
        this.localeKey = localeKey;
        this.adder = adder;
        this.remover = remover;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public DualConsumer<SplitPaneSystem, DockNode> getAdder() {
        return adder;
    }

    public DualConsumer<SplitPaneSystem, DockNode> getRemover() {
        return remover;
    }
}
