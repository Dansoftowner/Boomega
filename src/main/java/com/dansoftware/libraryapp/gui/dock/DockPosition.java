package com.dansoftware.libraryapp.gui.dock;

import com.dansoftware.libraryapp.gui.dock.docknode.DockNode;
import com.dansoftware.libraryapp.gui.dock.docksystem.SplitPaneSystem;
import com.dansoftware.libraryapp.util.function.DualConsumer;

public enum DockPosition {
    LEFT_TOP("dock.options.pos.left.top", (splitPaneSystem, dockNode) -> {
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

    LEFT_BOTTOM("dock.options.pos.left.bottom", (splitPaneSystem, dockNode) -> {
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

    BOTTOM_LEFT("dock.options.pos.bottom.left", (splitPaneSystem, dockNode) -> {
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

    BOTTOM_RIGHT("dock.options.pos.bottom.right", (splitPaneSystem, dockNode) -> {
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

    RIGHT_TOP("dock.options.pos.right.top", (splitPaneSystem, dockNode) -> {
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

    RIGHT_BOTTOM("dock.options.pos.right.bottom",(splitPaneSystem, dockNode) -> {
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

    TOP_RIGHT("dock.options.pos.top.right", (splitPaneSystem, dockNode) -> {
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

    TOP_LEFT("dock.options.pos.top.left", (splitPaneSystem, dockNode) -> {
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

    public static final String NAME_LOCALE_KEY = "dock.options.pos";

    private final String id;
    private final String localeKey;
    private final DualConsumer<SplitPaneSystem, DockNode> adder;
    private final DualConsumer<SplitPaneSystem, DockNode> remover;

    DockPosition(String localeKey, DualConsumer<SplitPaneSystem, DockNode> adder, DualConsumer<SplitPaneSystem, DockNode> remover) {
        this.localeKey = localeKey;
        this.adder = adder;
        this.remover = remover;
        this.id = this.toString().toLowerCase().replace('_', '-');
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

    public String getId() {
        return id;
    }
}
