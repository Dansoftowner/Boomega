package com.dansoftware.boomega.plugin.api;

import com.dansoftware.boomega.gui.theme.Theme;

/**
 * Allows to add a custom theme to the application.
 *
 * @author Daniel Gyoerffy
 */
public interface ThemePlugin extends BoomegaPlugin {
    Theme getTheme();
}
