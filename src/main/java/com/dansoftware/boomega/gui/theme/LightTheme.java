package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.gui.theme.applier.AndThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.JMetroThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.StyleSheetThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.ThemeApplier;
import com.dansoftware.boomega.i18n.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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

    private final ThemeApplier applier;

    public LightTheme() {
        this.applier = new AndThemeApplier(new JMetroThemeApplier(Style.LIGHT), new StyleSheetThemeApplier(getStyleSheets()));
    }

    @NotNull
    @Override
    protected ThemeApplier getApplier() {
        return applier;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private List<String> getStyleSheets() {
        return Collections.singletonList(getClass().getResource("light.css").toExternalForm());
    }

    public static ThemeMeta<LightTheme> getMeta() {
        return new ThemeMeta<>(LightTheme.class, () -> I18N.getValue("theme.light"), InternalThemeDesigner.INSTANCE);
    }
}
