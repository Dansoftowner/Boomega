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
            "/com/dansoftware/libraryapp/gui/theme/css/light/database-manager-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/first-time-dialog-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/global-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/global-workbench-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/info-view-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/login-view-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/notification-node-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/plugin-manager-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/update-dialog-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/google-books-module-light.css",
            "/com/dansoftware/libraryapp/gui/theme/css/light/record-add-form-light.css"
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
