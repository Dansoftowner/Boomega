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
            "/com/dansoftware/libraryapp/gui/theme/database-manager-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/first-time-dialog-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/global-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/global-workbench-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/info-view-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/login-view-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/notification-node-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/plugin-manager-dark.css",
            "/com/dansoftware/libraryapp/gui/theme/update-dialog-dark.css"
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
