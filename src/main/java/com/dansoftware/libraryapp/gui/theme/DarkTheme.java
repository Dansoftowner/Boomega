package com.dansoftware.libraryapp.gui.theme;

import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

/**
 * A DarkTheme is a {@link Theme} implementation that applies a dark-looking theme.
 *
 * <p>
 * The global {@link ThemeApplier} of this theme should be used for every gui-view in the application;
 * the custom {@link ThemeApplier} applies JMetro's dark style on the particular element;
 * it may not be used for every gui-object.
 *
 * @author Daniel Gyorffy
 */
public class DarkTheme extends Theme {

    private static final String GLOBAL_DARK_STYLE_SHEET = "/com/dansoftware/libraryapp/gui/theme/global-dark.css";

    public DarkTheme() {
        super();
    }

    @Override
    protected @NotNull ThemeApplier createGlobalApplier() {
        return new StyleSheetThemeApplier(GLOBAL_DARK_STYLE_SHEET);
    }

    @Override
    protected @NotNull ThemeApplier createCustomApplier() {
        return new JMetroThemeApplier(Style.DARK);
    }
}
