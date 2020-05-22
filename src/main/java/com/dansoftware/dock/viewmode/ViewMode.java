package com.dansoftware.dock.viewmode;

import java.util.function.Supplier;

public enum ViewMode {
    PINNED("pinned","dock.options.view.docked", DockStrategy::new),
    FLOAT("float","dock.options.view.float", FloatStrategy::new),
    WINDOW("window","dock.options.view.window", WindowStrategy::new);

    public static final String NAME_LOCALE_KEY = "dock.options.view.mode";

    private final String id;
    private final String localeKey;
    private final Supplier<ViewModeStrategy> viewModeStrategySupplier;

    ViewMode(String id, String localeKey, Supplier<ViewModeStrategy> viewModeStrategySupplier) {
        this.id = id;
        this.localeKey = localeKey;
        this.viewModeStrategySupplier = viewModeStrategySupplier;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public String getId() {
        return id;
    }

    public Supplier<ViewModeStrategy> getViewModeStrategySupplier() {
        return viewModeStrategySupplier;
    }

    public ViewModeStrategy getViewModeStrategy() {
        return viewModeStrategySupplier.get();
    }
}