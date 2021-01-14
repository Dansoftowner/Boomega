package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.locale.I18N;
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

    private static final ThemeMeta<DarkTheme> THEME_META =
            new ThemeMeta<>(DarkTheme.class, () -> I18N.getGeneralValue("theme.dark"), InternalThemeDesigner.INSTANCE);

    static {
        registerTheme(THEME_META);
    }

    private final List<String> styleSheets = List.of(
            "/com/dansoftware/libraryapp/gui/theme/css/dark/database-manager-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/first-time-dialog-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/global-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/global-workbench-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/info-view-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/login-view-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/notification-node-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/plugin-manager-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/update-dialog-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/google-books-module-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/css/dark/record-add-form-dark.css"
    );

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    public DarkTheme() {
        super();
        this.globalApplier = new StyleSheetThemeApplier(styleSheets);
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
