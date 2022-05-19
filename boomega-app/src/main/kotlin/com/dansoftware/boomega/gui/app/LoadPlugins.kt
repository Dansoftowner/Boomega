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

import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.plugin.api.PluginService
import javafx.util.Duration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.dansoftware.boomega.gui.app.LoadPluginsKt")

/**
 * Loads plugins into the memory
 */
fun BoomegaApp.loadPlugins() {
    notifyPreloader("preloader.plugins.load")

    val pluginService = get(PluginService::class)
    pluginService.load()

    val pluginFileCount = pluginService.pluginFileCount
    if (pluginFileCount > 0)
        postLaunch { context, _ ->
            context.showInformationNotification(
                title = i18n("plugins.read.count.title", pluginFileCount),
                message = null,
                Duration.minutes(1.0)
            )
        }

    logger.info("Plugins loaded successfully!")
}