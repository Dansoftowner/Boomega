package com.dansoftware.libraryapp.gui.theme;

import com.dansoftware.libraryapp.locale.I18N;
import jfxtras.styles.jmetro.Style;
import org.jetbrains.annotations.NotNull;

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

    private static final ThemeMeta<LightTheme> THEME_META =
            new ThemeMeta<>(LightTheme.class, () -> I18N.getGeneralValue("theme.light"), InternalThemeDesigner.INSTANCE);

    static {
        registerTheme(THEME_META);
    }

    private final List<String> styleSheets = List.of(
            "/com/dansoftware/libraryapp/gui/theme/database-manager-light.css",
            "/com/dansoftware/libraryapp/gui/theme/first-time-dialog-light.css",
            "/com/dansoftware/libraryapp/gui/theme/global-light.css",
            "/com/dansoftware/libraryapp/gui/theme/global-workbench-light.css",
            "/com/dansoftware/libraryapp/gui/theme/info-view-light.css",
            "/com/dansoftware/libraryapp/gui/theme/login-view-light.css",
            "/com/dansoftware/libraryapp/gui/theme/notification-node-light.css",
            "/com/dansoftware/libraryapp/gui/theme/plugin-manager-light.css",
            "/com/dansoftware/libraryapp/gui/theme/update-dialog-light.css",
            "/com/dansoftware/libraryapp/gui/theme/google-books-module-light.css"
    );

    private final ThemeApplier globalApplier;
    private final ThemeApplier customApplier;

    public LightTheme() {
        super();
        this.globalApplier = new StyleSheetThemeApplier(styleSheets);
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
