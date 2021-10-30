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

package com.dansoftware.boomega.gui.menu.file

import com.dansoftware.boomega.config.LOGIN_DATA
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.internalActivityLauncher
import com.dansoftware.boomega.util.concurrent.SingleThreadExecutor
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

/**
 * The base file menu. Supertype of all kind of os specific file-menus.
 */
abstract class FileMenu(
    private val context: Context,
    private val databaseMeta: DatabaseMeta,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) : Menu(i18n("menubar.menu.file")) {

    init {
        this.menuItem(newEntryMenuItem())
            .menuItem(openMenuItem())
            .menuItem(databaseCreatorMenuItem())
            .menuItem(databaseManagerMenuItem())
            .menuItem(recentDatabasesMenuItem())
            .menuItem(databaseCloseMenuItem())
            .separator()
            .menuItem(revealInExplorerMenuItem())
    }

    /**
     * Menu item that allows the user to show a new entry point (LoginActivity)
     */
    private fun newEntryMenuItem(): MenuItem =
        menuItemOf(GlobalActions.NEW_ENTRY, context, preferences, databaseTracker)

    /**
     * Menu item that allows the user to open a database file from the file system
     */
    private fun openMenuItem() =
        menuItemOf(GlobalActions.OPEN_DATABASE, context, preferences, databaseTracker)

    private fun databaseCreatorMenuItem() =
        menuItemOf(GlobalActions.CREATE_DATABASE, context, preferences, databaseTracker)

    private fun databaseManagerMenuItem() =
        menuItemOf(GlobalActions.OPEN_DATABASE_MANAGER, context, preferences, databaseTracker)

    /**
     * Menu that allows the user to access the recent databases
     */
    private fun recentDatabasesMenuItem(): MenuItem =
        object : Menu(i18n("menubar.menu.file.recent")) {

            private val recentMenu = this

            private val menuItemFactory: (DatabaseMeta) -> MenuItem = { db ->
                MenuItem(db.toString()).also { menuItem ->
                    menuItem.setOnAction {
                        startActivityLauncher {
                            internalActivityLauncher(
                                initialDatabase = db,
                                preferences = preferences,
                                databaseTracker = databaseTracker
                            )
                        }
                    }
                    menuItem.userData = db
                }
            }

            private val trackerObserver = object : DatabaseTracker.Observer {
                override fun onDatabaseAdded(db: DatabaseMeta) {
                    recentMenu.menuItem(menuItemFactory.invoke(db))
                }

                override fun onDatabaseRemoved(db: DatabaseMeta) {
                    recentMenu.items.find { menuItem -> menuItem.userData == db }?.also { menuItem ->
                        recentMenu.items.remove(menuItem)
                    }
                }
            }

            init {
                databaseTracker.savedDatabases.forEach { db -> this.menuItem(menuItemFactory.invoke(db)) }
                databaseTracker.registerObserver(trackerObserver)
                this.graphic("database-clock-icon")
            }
        }

    private fun databaseCloseMenuItem() = MenuItem(i18n("menubar.menu.file.dbclose"))
        .graphic("logout-icon")
        .action {
            preferences.editor()
                .put(LOGIN_DATA, preferences[LOGIN_DATA].apply {
                    if (isAutoLogin && selectedDatabase == databaseMeta) {
                        autoLoginCredentials = null
                    }
                }).tryCommit()
            GlobalActions.NEW_ENTRY.invoke(context, preferences, databaseTracker)
            context.close()
        }

    private fun revealInExplorerMenuItem() = MenuItem(i18n("menubar.menu.file.reveal"))
        .graphic("folder-open-icon")
        .apply { isDisable = databaseMeta.isActionSupported(DatabaseMeta.Action.OpenInExternalApplication) }
        .action { databaseMeta.performAction(DatabaseMeta.Action.OpenInExternalApplication) }

    private inline fun startActivityLauncher(crossinline getActivityLauncher: () -> ActivityLauncher) {
        SingleThreadExecutor.submit(object : Task<Unit>() {

            init {
                this.setOnRunning { context.showIndeterminateProgress() }
                this.setOnFailed { context.stopProgress() }
                this.setOnSucceeded { context.stopProgress() }
            }

            override fun call() {
                getActivityLauncher().launch()
            }
        })
    }
}