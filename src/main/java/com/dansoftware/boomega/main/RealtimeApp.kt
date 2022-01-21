/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.main

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.app.BoomegaApp
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.instance.ApplicationInstanceService
import com.dansoftware.boomega.plugin.PluginClassLoader
import com.dansoftware.boomega.plugin.Plugins
import javafx.util.Duration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * The real-time implementation of [BoomegaApp] that handles plugin-loading
 * from the disk, closes down the [ApplicationInstanceService] etc...
 */
class RealtimeApp : BoomegaApp() {

    override fun buildPreferences(): Preferences = DefaultPreferences
    override fun buildDatabaseTracker(): DatabaseTracker = DefaultDatabaseTracker

    override fun stop() {
        super.stop()

        logger.info("Shutting down application instance service")
        ApplicationInstanceService.release()

        logger.info("Closing down PluginClassLoader")
        PluginClassLoader.close()

        //We wait 5 seconds for the background processes to terminate, then we shut down explicitly the application
        //ExploitativeExecutor.INSTANCE.awaitTermination(1500, TimeUnit.MILLISECONDS);
        exitProcess(0)
    }

    override fun preInit() {
        loadPlugins()
    }

    /**
     * Loads plugins into the memory
     */
    private fun loadPlugins() {
        notifyPreloader("preloader.plugins.load")
        Plugins.getInstance().load()
        val pluginFilesRead = Plugins.getInstance().pluginFileCount()
        if (pluginFilesRead > 0) {
            postLaunch { context, _ ->
                context.showInformationNotification(
                    title = i18n("plugins.read.count.title", pluginFilesRead),
                    message = null,
                    Duration.minutes(1.0)
                )
            }
        }
        logger.info("Plugins loaded successfully!")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RealtimeApp::class.java)
    }
}