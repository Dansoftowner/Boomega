package com.dansoftware.libraryapp.gui.theme;

import org.jetbrains.annotations.NotNull;

/**
 * A Themeable can handle the theme apply-requests.
 *
 * @see Theme#applyDefault(Themeable)
 * @see Theme#apply(Themeable)
 */
public interface Themeable {
    void handleThemeApply(@NotNull ThemeApplier globalApplier, @NotNull ThemeApplier customApplier);
}
