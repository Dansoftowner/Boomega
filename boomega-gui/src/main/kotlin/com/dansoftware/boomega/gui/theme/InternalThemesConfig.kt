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

package com.dansoftware.boomega.gui.theme

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.util.resJson
import com.google.gson.JsonObject
import com.google.inject.ImplementedBy
import javax.inject.Singleton

/**
 * The entity representing the internal theme configuration.
 * Defines what is the theme to be used as default and gives the list of
 * internally available themes (typically read from the default configuration file).
 */
@ImplementedBy(DefaultThemesConfig::class)
internal open class InternalThemesConfig(json: JsonObject) {

    /**
     * The class-name of the theme should be used as default
     */
    val defaultThemeClassName: String = json[DEFAULT_THEME_CLASS_NAME].asString

    /**
     * All internal theme class-names available for the app
     */
    val themeClassNames: List<String> by lazy {
        listOf(defaultThemeClassName) + json[THEME_CLASS_NAMES].asJsonArray.map { it.asString }
    }

    /**
     * The default theme instantiated
     */
    val defaultTheme: Theme by lazy { construct(defaultThemeClassName) }

    /**
     * All the internal themes instantiated
     */
    val themes: List<Theme> by lazy { themeClassNames.map(::construct) }

    private fun construct(className: String) = get(parseClass(className))

    @Suppress("UNCHECKED_CAST")
    private fun parseClass(className: String) = Class.forName(className) as Class<out Theme>

    companion object {
        const val DEFAULT_THEME_CLASS_NAME = "defaultThemeClassName";
        const val THEME_CLASS_NAMES = "themeClassNames"
    }
}

/**
 * The themes-configuration object reads from the default configuration file
 */
@Singleton
private class DefaultThemesConfig : InternalThemesConfig(resJson("internal_themes.json", this::class).asJsonObject)