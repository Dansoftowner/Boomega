package com.dansoftware.boomega.plugin.api

import com.dansoftware.boomega.gui.theme.Theme

/**
 * Allows to add a custom theme to the application.
 *
 * @author Daniel Gyoerffy
 */
interface ThemePlugin : BoomegaPlugin {
    val theme: Theme
}