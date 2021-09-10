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

package com.dansoftware.boomega.gui.menubar

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.action.MenuItems
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.LauncherMode
import com.dansoftware.boomega.util.concurrent.SingleThreadExecutor
import com.dansoftware.boomega.util.revealInExplorer
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

/**
 * The base file menu, that's used on MacOS.
 * The file menu used on Windows/Linux is the [RegularFileMenu] which
 * is a subtype of the [FileMenu].
 */
open class FileMenu(
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
        MenuItems.of(GlobalActions.NEW_ENTRY, context, preferences, databaseTracker)

    /**
     * Menu item that allows the user to open a database file from the file system
     */
    private fun openMenuItem() =
        MenuItems.of(GlobalActions.OPEN_DATABASE, context, preferences, databaseTracker)

    private fun databaseCreatorMenuItem() =
        MenuItems.of(GlobalActions.CREATE_DATABASE, context, preferences, databaseTracker)

    private fun databaseManagerMenuItem() =
        MenuItems.of(GlobalActions.OPEN_DATABASE_MANAGER, context, preferences, databaseTracker)

    /**
     * Menu that allows the user to access the recent databases
     */
    private fun recentDatabasesMenuItem(): MenuItem =
        object : Menu(i18n("menubar.menu.file.recent")) {
            private val it = this
            private val menuItemFactory: (DatabaseMeta) -> MenuItem = { db ->
                MenuItem(db.toString()).also { menuItem ->
                    menuItem.setOnAction {
                        startActivityLauncher {
                            ActivityLauncher(
                                LauncherMode.INTERNAL,
                                db,
                                preferences,
                                databaseTracker
                            )
                        }
                    }
                    menuItem.userData = db
                }
            }
            private val trackerObserver = object : DatabaseTracker.Observer {
                override fun onDatabaseAdded(db: DatabaseMeta) {
                    it.menuItem(menuItemFactory.invoke(db))
                }

                override fun onDatabaseRemoved(db: DatabaseMeta) {
                    it.items.find { menuItem -> menuItem.userData == db }?.also { menuItem ->
                        it.items.remove(menuItem)
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
        .action {
            preferences.editor()
                .put(PreferenceKey.LOGIN_DATA, preferences.get(PreferenceKey.LOGIN_DATA).apply {
                    if (autoLoginDatabase == databaseMeta) {
                        isAutoLogin = false
                        autoLoginCredentials = null
                    }
                }).tryCommit()
            GlobalActions.NEW_ENTRY.invoke(context, preferences, databaseTracker)
            context.close()
        }
        .graphic("logout-icon")

    private fun revealInExplorerMenuItem() = MenuItem(i18n("menubar.menu.file.reveal"))
        .action { databaseMeta.file!!.revealInExplorer() }
        .graphic("folder-open-icon")

    private fun startActivityLauncher(getActivityLauncher: () -> ActivityLauncher) {
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