/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.app

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.config.THEME
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.LOCALE
import com.dansoftware.boomega.i18n.api.I18N
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.dansoftware.boomega.gui.app.ApplyConfigurationsKt")

/**
 * Applies configurations should be applied when the app starts
 */
fun BoomegaApp.applyConfigurations() {
    applyBaseConfigurations()
    applyAdditionalConfigurations()
}

private fun BoomegaApp.applyBaseConfigurations() {
    val preferences = get(Preferences::class)
    notifyPreloader("preloader.lang")
    I18N.setLocale(preferences[LOCALE])
    notifyPreloader("preloader.theme")
    Theme.default = preferences[THEME]
}

private fun applyAdditionalConfigurations() {
    val preferences = get(Preferences::class)
    fun applyWindowsOpacity() {
        val opacity = preferences[BaseWindow.GLOBAL_OPACITY_CONFIG_KEY]
        logger.debug("Global window opacity read: {}", opacity)
        BaseWindow.globalOpacity.set(opacity)
    }
    applyWindowsOpacity()
    KeyBindings.loadFrom(preferences)
}