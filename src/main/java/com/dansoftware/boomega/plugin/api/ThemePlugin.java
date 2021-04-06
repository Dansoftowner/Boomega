package com.dansoftware.boomega.plugin.api;

import com.dansoftware.boomega.gui.theme.Theme;
import org.jetbrains.annotations.NotNull;

/**
 * Allows to add a custom theme to the application.
 *
 * @author Daniel Gyoerffy
 */
public interface ThemePlugin extends BoomegaPlugin {
    @NotNull
    Theme getTheme();
}
