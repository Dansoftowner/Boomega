package com.dansoftware.libraryapp.gui.dock;

public enum ViewMode {
    PINNED("dock.options.view.docked"), FLOAT("dock.options.view.float"), WINDOW("dock.options.view.window");

    public static final String NAME_LOCALE_KEY = "dock.options.view.mode";

    private final String id;
    private final String localeKey;

    ViewMode(String localeKey) {
        this.id = this.toString().toLowerCase();
        this.localeKey = localeKey;
    }

    public String getLocaleKey() {
        return localeKey;
    }

    public String getId() {
        return id;
    }
}