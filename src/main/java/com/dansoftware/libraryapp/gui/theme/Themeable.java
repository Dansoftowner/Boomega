package com.dansoftware.libraryapp.gui.theme;

/**
 * A Themeable can handle the theme apply-requests.
 *
 * @see Theme#applyDefault(Themeable)
 * @see Theme#apply(Themeable)
 */
public interface Themeable {

    void handleThemeApply(Theme newTheme);
}
