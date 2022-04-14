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

package com.dansoftware.boomega.gui.launch

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.DatabaseConstructionException
import com.dansoftware.boomega.database.api.DatabaseField
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.bmdb.BMDBProvider
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseActivity
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker
import com.dansoftware.boomega.gui.entry.EntryActivity
import com.dansoftware.boomega.gui.login.DatabaseLoginListener
import com.dansoftware.boomega.gui.login.LoginActivity
import com.dansoftware.boomega.gui.login.config.LOGIN_DATA
import com.dansoftware.boomega.gui.login.config.LoginData
import com.dansoftware.boomega.gui.login.quick.QuickLoginActivity
import com.dansoftware.boomega.i18n.api.i18n
import javafx.application.Platform
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class ActivityLauncher(
    val mode: LauncherMode = LauncherMode.INITIAL,
    val preferences: com.dansoftware.boomega.config.Preferences,
    val databaseTracker: DatabaseTracker,
    val initialDatabase: DatabaseMeta? = null
) : Runnable {

    /**
     * Provides a way to retrieve & save the login-data configuration
     */
    protected open var loginData: LoginData
        get() = preferences[LOGIN_DATA]
        set(value) {
            preferences.editor().put(LOGIN_DATA, value).tryCommit()
        }

    /**
     * Called when a new database (from the arguments) is added to the login-data.
     *
     * <p>
     * The base method adds the {@link DatabaseMeta} to the {@link DatabaseTracker}
     * and saves it into the [LoginData] if necessary.
     *
     * @param meta the meta-information representing the database
     */
    protected open fun onNewDatabaseAdded(meta: DatabaseMeta) {
        val newLoginData = loginData
        when {
            newLoginData.savedDatabases.contains(meta).not() -> {
                logger.debug("saving the launched database into the login data...")
                newLoginData.savedDatabases.add(meta)
                databaseTracker.saveDatabase(meta)
                loginData = newLoginData
            }
            else -> logger.debug("The launched database is already in the login data")
        }
    }

    /**
     * Called on the UI-thread, when an "Activity" is launched.
     *
     * @param context the gui-context
     * @param launchedDatabase the launched database; may be null
     */
    protected open fun onActivityLaunched(context: Context, launchedDatabase: DatabaseMeta? = null) {
    }

    override fun run() = launch()

    /**
     * Launches the activity based on the given conditions
     */
    fun launch() {
        logger.debug("{} mode detected", mode)
        initialDatabase?.let {
            logger.debug("Argument found")
            handleArgument(initialDatabase = it)
        } ?: run {
            handleNoArgument()
        }
    }

    /**
     * Handles the argument (the initial database) depending on the launcher-mode.
     */
    private fun handleArgument(mode: LauncherMode = this.mode, initialDatabase: DatabaseMeta) {
        when (mode) {
            LauncherMode.INITIAL -> handleArgumentInit(initialDatabase)
            LauncherMode.EXTERNAL -> handleArgumentExternal(initialDatabase)
            LauncherMode.INTERNAL -> handleArgumentInternal(initialDatabase)
        }
    }

    /**
     * Handles the argument (the initial database) assuming the launcher-mode is [LauncherMode.INITIAL]
     */
    private fun handleArgumentInit(meta: DatabaseMeta) {
        onNewDatabaseAdded(meta)
        logger.debug("trying to sign in into the database...")
        val database = constructDatabase(meta) { e ->
            Platform.runLater {
                /* val temp = loginData
                 //we select it, but we don't save it to the configurations
                 temp.selectedDatabase = meta*/

                showEntryActivity {
                    context.showErrorDialog(
                        title = i18n("login.failed"),
                        message = e.localizedMessage ?: "",
                        e
                    )
                }
            }
        }

        database?.let {
            logger.debug("signed in into the argument-database successfully, launching a MainActivity...")
            Platform.runLater {
                showDatabaseActivity(it)
            }
        }
    }

    /**
     * Handles the argument (the initial database) assuming the launcher-mode is [LauncherMode.EXTERNAL]
     */
    private fun handleArgumentExternal(meta: DatabaseMeta) {
        DatabaseActivity.getByDatabase(meta)
            .map(DatabaseActivity::context)
            .ifPresentOrElse({ Platform.runLater(it::toFrontRequest) }) {
                handleArgumentInit(meta)
            }
    }

    /**
     * Handles the argument (the initial database) assuming the launcher-mode is [LauncherMode.INTERNAL]
     */
    private fun handleArgumentInternal(meta: DatabaseMeta) {
        DatabaseActivity.getByDatabase(meta)
            .map(DatabaseActivity::context)
            .ifPresentOrElse({
                logger.debug("Found gui-context for database: '{}'", meta.identifier)
                Platform.runLater(it::toFrontRequest)
            }) {
                logger.debug("Didn't found GUI for database: '{}'", meta.identifier)
                onNewDatabaseAdded(meta)

                val fallbackDatabaseConstruction = {
                    Platform.runLater {
                        showQuickLoginActivity(meta, loginListener = {
                            showDatabaseActivity(it)
                        })
                    }
                }

                val database = when {
                    // TODO: allow somehow to configure databases where anonymous login is allowed
                    meta.provider.equals(BMDBProvider) -> constructDatabase(meta) { fallbackDatabaseConstruction() }
                    else -> fallbackDatabaseConstruction().let { null }
                }

                database?.let {
                    logger.debug("Signed into the argument-database successfully, launching a MainActivity...")
                    Platform.runLater {
                        showDatabaseActivity(it)
                    }
                }

            }
    }

    private fun handleNoArgument(mode: LauncherMode = this.mode) {
        when (mode) {
            LauncherMode.INITIAL -> handleNoArgumentInit()
            LauncherMode.EXTERNAL -> handleNoArgumentExternal()
            LauncherMode.INTERNAL -> handleNoArgumentInternal()
        }
    }

    private fun handleNoArgumentInit() {
        //if there was no application-argument
        //it is basically a normal application-start.
        if (loginData.isAutoLogin) {
            //if auto login is turned on
            logger.debug("auto login is turned on, trying to sign in into the database...")
            autoLogin()
        } else {
            //if auto login is turned off
            logger.debug("auto-login is turned off, launching a basic EntryActivity...")
            Platform.runLater {
                showEntryActivity()
            }
        }
    }

    /**
     * Handles the situation when there is no application-argument
     * assuming that the launcher-mode is {@link LauncherMode#ALREADY_RUNNING}
     */
    private fun handleNoArgumentExternal() {
        //no argument
        //just focusing on a random window
        logger.debug("no argument found, focusing on a random window...")

        Platform.runLater {
            EntryActivity.getShowingEntries()
                .stream()
                .limit(1)
                .findAny()
                .map(EntryActivity::getContext)
                .ifPresent(Context::toFrontRequest)
        }
    }

    private fun handleNoArgumentInternal() {
        Platform.runLater {
            LoginActivity.getActiveLoginActivities().stream()
                .map(LoginActivity::getContext)
                .findAny()
                .ifPresentOrElse({
                    it.toFrontRequest()
                    onActivityLaunched(it)
                }, ::showEntryActivity)
        }
    }

    private fun autoLogin() {
        val database = constructDatabase(
            loginData.selectedDatabase!!,
            loginData.autoLoginCredentials!!
        ) { e ->
            logger.debug("failed signing into the database")
            Platform.runLater {
                showEntryActivity {
                    context.showErrorDialog(i18n("login.failed"), e.localizedMessage ?: "", e)
                }
            }
        }
        database?.let {
            logger.debug("signed in into the auto-login database successfully, launching a MainActivity...")

            Platform.runLater {
                showDatabaseActivity(it)
            }
        }
    }

    private inline fun constructDatabase(
        meta: DatabaseMeta,
        credentials: Map<DatabaseField<*>, Any> = emptyMap(),
        onFailed: (DatabaseConstructionException) -> Unit
    ): Database? {
        return try {
            meta.provider.getDatabase(meta, credentials, options = emptyMap()) // TODO: database options!
        } catch (e: DatabaseConstructionException) {
            onFailed(e)
            null
        }
    }

    private inline fun showEntryActivity(onShown: EntryActivity.() -> Unit = {}) =
        EntryActivity().apply {
            show()
            onShown(this)
            onActivityLaunched(context)
        }


    private inline fun showDatabaseActivity(database: Database, onShown: DatabaseActivity.() -> Unit = {}) =
        DatabaseActivity(database).apply {
            show()
            onShown(this)
            onActivityLaunched(context, database.meta)
        }

    private inline fun showQuickLoginActivity(
        meta: DatabaseMeta,
        loginListener: DatabaseLoginListener,
        onShown: QuickLoginActivity.() -> Unit = {}
    ) {
        QuickLoginActivity(meta, loginListener).apply {
            show()
            onShown(this)
            onActivityLaunched(context, meta)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ActivityLauncher::class.java)
    }
}