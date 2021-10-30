/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
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
import com.dansoftware.boomega.config.logindata.LoginData
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.firsttime.FirstTimeActivity
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader
import com.dansoftware.boomega.gui.preloader.BoomegaPreloader.MessageNotification.Priority
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.window.BaseWindow
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.instance.ApplicationInstanceService
import com.dansoftware.boomega.launcher.initActivityLauncher
import com.dansoftware.boomega.plugin.PluginClassLoader
import com.dansoftware.boomega.plugin.Plugins
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
import kotlin.system.exitProcess

/**
 * The Boomega javafx application implementation
 */
class BoomegaApp : BaseBoomegaApplication() {

    /**
     * The queue that stores the actions should be invoked after an activity is launched
     */
    private val postLaunchQueue: Queue<(context: Context, launchedDatabase: DatabaseMeta?) -> Unit> = LinkedList()

    override fun init() {

        handleApplicationArgument()
        progress(0.2)

        loadPlugins()
        progress(0.4)

        val preferences = readConfigurations()
        progress(0.6)

        if (showFirstTimeActivity(preferences).not())
            applyBaseConfigurations(preferences)
        applyAdditionalConfigurations(preferences)
        progress(0.8)

        logger.debug("Theme is: {}", Theme.default)
        logger.debug("Locale is: {}", Locale.getDefault())

        val databaseTracker = DatabaseTracker.getGlobal()
        val loginData = readLoginData(preferences, databaseTracker)
        progress(0.9)

        // searching for updates
        val update = searchForUpdates(preferences)
        progress(1.0)

        launchGUI(preferences, databaseTracker, loginData, update)
    }

    override fun stop() {
        //writing all configurations
        logger.info("Saving configurations")
        val preferences = Preferences.getPreferences()
        preferences.editor.commit()

        logger.info("Shutting down application instance service")
        ApplicationInstanceService.release()

        logger.info("Closing down PluginClassLoader")
        PluginClassLoader.close()

        //We wait 5 seconds for the background processes to terminate, then we shut down explicitly the application
        //ExploitativeExecutor.INSTANCE.awaitTermination(1500, TimeUnit.MILLISECONDS);
        exitProcess(0)
    }

    private fun handleApplicationArgument() {
        // Showing message on the preloader about the launched database
        launchedDatabase?.let {
            notifyPreloader(
                BoomegaPreloader.MessageNotification(
                    message = i18n("preloader.file.open", it.simpleName),
                    priority = Priority.HIGH
                )
            )
        }

        // Showing a notification after the application starts about the launched database
        postLaunchQueue.offer { context, launched ->
            launched?.let {
                context.showInformationNotification(
                    title = i18n("database.file.launched", launched.simpleName),
                    message = null
                )
            }
        }
    }

    /**
     * Loads plugins into the memory
     */
    private fun loadPlugins() {
        notifyPreloader("preloader.plugins.load")
        Plugins.getInstance().load()
        val pluginFilesRead = Plugins.getInstance().pluginFileCount()
        if (pluginFilesRead > 0) {
            postLaunchQueue.offer { context, _ ->
                context.showInformationNotification(
                    title = i18n("plugins.read.count.title", pluginFilesRead),
                    message = null,
                    Duration.minutes(1.0)
                )
            }
        }
        logger.info("Plugins loaded successfully!")
    }

    private fun readConfigurations(): Preferences {
        notifyPreloader("preloader.preferences.read")
        return try {
            Preferences.getPreferences().also {
                logger.info("Configurations has been read successfully!")
            }
        } catch (e: RuntimeException) {
            logger.error("Couldn't read configurations ", e)
            postLaunchQueue.offer { context, _ ->
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
    private fun applyBaseConfigurations(preferences: Preferences) {
        notifyPreloader("preloader.lang")
        Locale.setDefault(preferences[LOCALE])
        notifyPreloader("preloader.theme")
        Theme.default = preferences[THEME]
    }

    /**
     * Applies some configurations should be applied even if no [FirstTimeActivity] was shown.
     */
    private fun applyAdditionalConfigurations(preferences: Preferences) {
        fun applyWindowsOpacity() {
            val opacity = preferences[BaseWindow.GLOBAL_OPACITY_CONFIG_KEY]
            logger.debug("Global window opacity read: {}", opacity)
            BaseWindow.globalOpacity.set(opacity)
        }
        applyWindowsOpacity()
        KeyBindings.loadFrom(preferences)
    }

    /**
     * Reads the [LoginData] from the [Preferences] and updates the [DatabaseTracker].
     *
     * @return the read [LoginData]
     */
    private fun readLoginData(preferences: Preferences, databaseTracker: DatabaseTracker): LoginData {
        //adding the saved databases from the login-data to the database tracker
        notifyPreloader("preloader.logindata")
        val loginData = preferences[LOGIN_DATA]
        loginData.savedDatabases.forEach(databaseTracker::saveDatabase)
        return loginData
    }

    /**
     * Searches for updates if necessary
     */
    private fun searchForUpdates(preferences: Preferences): Release? {
        return when {
            preferences[SEARCH_UPDATES] -> {
                notifyPreloader("preloader.update.search")
                val updateSearcher = UpdateSearcher.default
                preferences.editor[LAST_UPDATE_SEARCH] = LocalDateTime.now()
                updateSearcher.trySearch { e -> logger.error("Couldn't search for updates", e) }
            }
            else -> null
        }
    }

    /**
     * Shows the [FirstTimeActivity] if needed and hides/resumes the Preloader when necessary.
     *
     * @return `true` if the first time dialog was shown; `false` otherwise
     */
    private fun showFirstTimeActivity(preferences: Preferences): Boolean {
        val lock = Any()
        return synchronized(lock) {
            when {
                FirstTimeActivity.isNeeded(preferences) -> {
                    hidePreloader()
                    logger.debug("First time dialog is needed")
                    Platform.runLater {
                        synchronized(lock) {
                            FirstTimeActivity(preferences).show()
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
    private fun launchGUI(
        preferences: Preferences,
        databaseTracker: DatabaseTracker,
        loginData: LoginData,
        update: Release?
    ) {
        notifyPreloader("preloader.gui.build")
        initActivityLauncher(
            preferences = preferences,
            databaseTracker = databaseTracker,
            applicationArgs = applicationArgs,
            getLoginData = { loginData },
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