package com.dansoftware.libraryapp.gui.theme;

import org.jetbrains.annotations.NotNull;

/**
 * A Themeable can handle the theme apply-requests.
 *
 * @author Daniel Gyorffy
 */
public interface Themeable {

    /**
     * Called when a new {@link Theme} is set default
     *
     * @param oldTheme the object that represents the old theme
     * @param newTheme the theme object
     */
    void handleThemeApply(@NotNull Theme oldTheme, @NotNull Theme newTheme);

    default void handleThemeApply(@NotNull Theme newTheme) {
        handleThemeApply(Theme.empty(), newTheme);
    }
}
