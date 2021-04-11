package com.dansoftware.boomega.gui.theme;

import com.dansoftware.boomega.gui.theme.applier.AndThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.JMetroThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.StyleSheetThemeApplier;
import com.dansoftware.boomega.gui.theme.applier.ThemeApplier;
import com.dansoftware.boomega.i18n.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    private final ThemeApplier applier;

    public DarkTheme() {
        this.applier = new AndThemeApplier(new JMetroThemeApplier(Style.DARK), new StyleSheetThemeApplier(getStyleSheets()));
    }

    @Override
    protected @NotNull ThemeApplier getApplier() {
        return applier;
    }

    @NotNull
    private List<String> getStyleSheets() {
        return List.of(
                "/com/dansoftware/boomega/gui/theme/css/global.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/database-manager-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/first-time-dialog-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/global-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/global-workbench-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/info-view-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/login-view-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/notification-node-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/plugin-manager-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/update-dialog-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/google-books-module-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/record-add-form-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/dock-system-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/records-view-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/preferences-dark.css",
                "/com/dansoftware/boomega/gui/theme/css/dark/markdown-dark.css"
        );
    }

    public static ThemeMeta<DarkTheme> getMeta() {
        return new ThemeMeta<>(DarkTheme.class, () -> I18N.getValue("theme.dark"), InternalThemeDesigner.INSTANCE);
    }
}
