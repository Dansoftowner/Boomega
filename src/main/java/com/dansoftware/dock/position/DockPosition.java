package com.dansoftware.dock.position;

import com.dansoftware.dock.position.strategy.*;

public enum DockPosition {
    LEFT_TOP("dock.options.pos.left.top", new LeftTopStrategy()),

    LEFT_BOTTOM("dock.options.pos.left.bottom", new LeftBottomStrategy()),

    BOTTOM_LEFT("dock.options.pos.bottom.left", new BottomLeftStrategy()),

    BOTTOM_RIGHT("dock.options.pos.bottom.right", new BottomRightStrategy()),

    RIGHT_TOP("dock.options.pos.right.top", new RightTopStrategy()),

    RIGHT_BOTTOM("dock.options.pos.right.bottom", new RightBottomStrategy()),

    TOP_RIGHT("dock.options.pos.top.right", new TopRightStrategy()),

    TOP_LEFT("dock.options.pos.top.left", new TopLeftStrategy());

    public static final String NAME_LOCALE_KEY = "dock.options.pos";

    private final String id;
    private final String localeKey;
    private final PositioningStrategy posStrategy;

    DockPosition(String localeKey, PositioningStrategy posStrategy) {
        this.localeKey = localeKey;
        this.posStrategy = posStrategy;
        this.id = this.toString().toLowerCase().replace('_', '-');
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public PositioningStrategy getPosStrategy() {
        return this.posStrategy;
    }

    public String getId() {
        return id;
    }
}
