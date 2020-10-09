package com.dansoftware.libraryapp.gui.theme;

/**
 * A Themeable can handle the theme apply-requests.
 *
 * @author Daniel Gyorffy
 */
public interface Themeable {

    /**
     * Called when a new {@link Theme} is set default
     *
     * @param newTheme the theme object
     */
    void handleThemeApply(Theme newTheme);
}
