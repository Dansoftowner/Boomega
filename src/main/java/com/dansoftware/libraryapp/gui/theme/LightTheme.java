package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.locale.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

/**
 * A LightTheme is a {@link Theme} implementation that applies a light theme.
 * It's also considered to be the default application theme.
 *
 * <p>
 * The global {@link ThemeApplier} of this theme should be used for every gui-view in the application;
 * the custom {@link ThemeApplier} applies JMetro's light style on the particular element;
 * it may not be used for every gui-object.
 *
 * @author Daniel Gyorffy
 */
public class LightTheme extends Theme {

    private static final String GLOBAL_LIGHT_STYLE_SHEET = "/com/dansoftware/libraryapp/gui/theme/global-light.css";

    private static final ThemeMeta<LightTheme> THEME_META =
            new ThemeMeta<>(LightTheme.class, () -> I18N.getGeneralValue("theme.light"), InternalThemeDesigner.INSTANCE);

    static {
        registerTheme(THEME_META);
    }

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    public LightTheme() {
        super();
        this.globalApplier = new StyleSheetThemeApplier(GLOBAL_LIGHT_STYLE_SHEET);
        this.customApplier = new JMetroThemeApplier(Style.LIGHT);
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
