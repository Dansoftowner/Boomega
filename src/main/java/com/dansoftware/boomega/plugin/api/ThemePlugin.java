package com.dansoftware.boomega.plugin.api;

import com.dansoftware.boomega.gui.theme.Theme;
import com.dansoftware.boomega.gui.theme.ThemeMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Allows to add a custom theme to the application.
 *
 * @author Daniel Gyoerffy
 */
public interface ThemePlugin extends BoomegaPlugin {
    @NotNull
    ThemeMeta<? extends Theme> getThemeMeta();
    @NotNull
    Theme getTheme();
}
