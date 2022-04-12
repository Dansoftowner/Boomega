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
import com.dansoftware.boomega.plugin.ThemePlugin
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.plugin.api.of
import com.dansoftware.boomega.util.toImmutableList
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Theme::class.java)

/**
 * The list of the available [Theme]s can be used by the app.
 * Includes built-in and plugin themes as well.
 */
object AvailableThemes : List<Theme> by loadThemes().toImmutableList()

/**
 * Gives a list of built-in themes and third-party themes
 */
private fun loadThemes(): List<Theme> =
    builtInThemes().plus(pluginThemes())
        .distinctBy(Any::javaClass)
        .onEach { logger.info("Found theme: '{}'", it::class.java.name) }
        .toList()

/**
 * Gives a [Sequence] of built-in themes
 */
private fun builtInThemes(): Sequence<Theme> =
    sequenceOf(
        LightTheme.INSTANCE,
        DarkTheme.INSTANCE,
        OsSynchronizedTheme.INSTANCE
    )

/**
 * Gives a [Sequence] of third-party themes read from plugins
 */
private fun pluginThemes(): Sequence<Theme> {
    logger.debug("Checking plugins for themes...")
    return get(PluginService::class)
        .of(ThemePlugin::class)
        .asSequence()
        .map(ThemePlugin::theme)
}