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
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker
import com.dansoftware.boomega.gui.launch.LauncherMode
import com.dansoftware.boomega.gui.launch.activityLauncher
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.main.parseArguments
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.update.Release
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * The flexible Boomega application implementation
 *
 * @see [com.dansoftware.boomega.main.RealtimeApp]
 */
open class BoomegaApp : BaseBoomegaApplication() {

    /**
     * The queue that stores the actions should be invoked after an activity is launched
     */
    private val postLaunchQueue: Queue<(context: Context, launchedDatabase: DatabaseMeta?) -> Unit> = LinkedList()

    /**
     * Queues the given action to be executed after the application launches successfully.
     * > It's public to make it available for extension functions.
     */
    fun postLaunch(action: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit) {
        postLaunchQueue.offer(action)
    }

    override fun init() {

        loadPlugins()
        progress(0.2)

        handleApplicationArgument()
        progress(0.4)

        readConfigurations()
        progress(0.6)

        showSetupWizard()
        applyConfigurations()
        progress(0.8)

        logger.debug("Theme is: {}", Theme.default)
        logger.debug("Locale is: {}", Locale.getDefault())

        progress(0.9)

        // searching for updates
        val update = searchForUpdates()
        progress(1.0)

        launchGUI(update)
    }

    override fun stop() {
        //writing all configurations
        logger.info("Saving configurations")
        get(Preferences::class).editor.commit()

        logger.info("Closing down plugin service")
        get(PluginService::class).close()
    }

    private fun readConfigurations(): Preferences {
        notifyPreloader("preloader.preferences.read")
        return try {
            get(Preferences::class).also {
                logger.info("Configurations has been read successfully!")
            }
        } catch (e: RuntimeException) {
            logger.error("Couldn't read configurations ", e)
            postLaunch { context, _ ->
                context.showErrorNotification(
                    title = i18n("preferences.read.failed.title"),
                    message = null,
                    onClicked = {
                        context.showErrorDialog(
                            title = i18n("preferences.read.failed.title"),
                            message = i18n("preferences.read.failed.msg"),
                            cause = e
                        )
                    }
                )
            }
            Preferences.empty()
        }
    }

    /**
     * Launches the main UI environment
     */
    private fun launchGUI(update: Release?) {
        notifyPreloader("preloader.gui.build")
        activityLauncher(
            mode = LauncherMode.INITIAL,
            preferences = get(Preferences::class),
            databaseTracker = get(DatabaseTracker::class),
            initialDatabase = parseArguments(applicationArgs),
            onLaunched = { context, launched ->
                update?.let {
                    val updateActivity =
                        UpdateActivity(context, it)
                    updateActivity.show()
                }
                while (!postLaunchQueue.isEmpty())
                    postLaunchQueue.poll()(context, launched)
            }
        ).launch()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BoomegaApp::class.java)
    }

}