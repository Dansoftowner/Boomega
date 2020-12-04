package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.locale.I18N;
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

    private static final ThemeMeta<DarkTheme> THEME_META =
            new ThemeMeta<>(DarkTheme.class, () -> I18N.getGeneralValue("theme.dark"), InternalThemeDesigner.INSTANCE);

    static {
        registerTheme(THEME_META);
    }

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    public DarkTheme() {
        super();
        this.globalApplier = new StyleSheetThemeApplier(GLOBAL_DARK_STYLE_SHEET);
        this.customApplier = new JMetroThemeApplier(Style.DARK);
    }

    @Override
    public @NotNull ThemeApplier getGlobalApplier() {
        return this.globalApplier;
    }

    @Override
    public @NotNull ThemeApplier getCustomApplier() {
        return this.customApplier;
    }
}
