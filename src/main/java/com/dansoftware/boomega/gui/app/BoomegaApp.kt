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

import com.dansoftware.boomega.config.*
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.firsttime.FirstTimeActivity
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader.MessageNotification.Priority
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.launcher.initActivityLauncher
import com.dansoftware.boomega.plugin.api.PluginService
import com.dansoftware.boomega.update.Release
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.concurrent.notify
import com.dansoftware.boomega.util.concurrent.wait
import javafx.application.Platform
import javafx.util.Duration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
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
     * Queues the given action to be executed after the application launches successfully
     */
    protected fun postLaunch(action: (context: Context, launchedDatabase: DatabaseMeta?) -> Unit) {
        postLaunchQueue.offer(action)
    }

    override fun init() {

        loadPlugins()
        progress(0.2)

        handleApplicationArgument()
        progress(0.4)

        readConfigurations()
        progress(0.6)

        if (showFirstTimeActivity().not())
            applyBaseConfigurations()
        applyAdditionalConfigurations()
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

    private fun handleApplicationArgument() {
        // Showing message on the preloader about the launched database
        launchedDatabase?.let {
            notifyPreloader(
                BoomegaPreloader.MessageNotification(
                    message = i18n("preloader.file.open", it.name),
                    priority = Priority.HIGH
                )
            )
        }

        // Showing a notification after the application starts about the launched database
        postLaunch { context, launched ->
            launched?.let {
                context.showInformationNotification(
                    title = i18n("database.file.launched", launched.name),
                    message = null
                )
            }
        }
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
     * Reads some configurations from the [Preferences] and applies them.
     * It should be only invoked if the [FirstTimeActivity] was not shown.
     */
    private fun applyBaseConfigurations() {
        val preferences = get(Preferences::class)
        notifyPreloader("preloader.lang")
        Locale.setDefault(preferences[LOCALE])
        notifyPreloader("preloader.theme")
        Theme.default = preferences[THEME]
    }

    /**
     * Applies some configurations should be applied even if no [FirstTimeActivity] was shown.
     */
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

    /**
     * Searches for updates if necessary
     */
    private fun searchForUpdates(): Release? {
        val preferences = get(Preferences::class)
        return when {
            preferences[SEARCH_UPDATES] -> {
                notifyPreloader("preloader.update.search")
                val updateSearcher = get(UpdateSearcher::class)
                preferences.editor[LAST_UPDATE_SEARCH] = LocalDateTime.now()
                updateSearcher.trySearch { e -> logger.error("Couldn't search for updates", e) }
            }
            else -> null
        }
    }

    /**
     * Loads plugins into the memory
     */
    private fun loadPlugins() {
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

    /**
     * Shows the [FirstTimeActivity] if needed and hides/resumes the Preloader when necessary.
     *
     * @return `true` if the first time dialog was shown; `false` otherwise
     */
    private fun showFirstTimeActivity(): Boolean {
        val lock = Any()
        return synchronized(lock) {
            when {
                FirstTimeActivity.isNeeded(get(Preferences::class)) -> {
                    hidePreloader()
                    logger.debug("First time dialog is needed")
                    Platform.runLater {
                        synchronized(lock) {
                            FirstTimeActivity(get(Preferences::class)).show()
                            lock.notify()
                        }
                    }
                    // waiting till the first time dialog completes
                    lock.wait()
                    showPreloader()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Launches the main UI environment
     */
    private fun launchGUI(update: Release?) {
        notifyPreloader("preloader.gui.build")
        initActivityLauncher(
            preferences = get(Preferences::class),
            databaseTracker = get(DatabaseTracker::class),
            applicationArgs = applicationArgs,
            onLaunched = { context, launched ->
                update?.let {
                    val updateActivity = UpdateActivity(context, it)
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