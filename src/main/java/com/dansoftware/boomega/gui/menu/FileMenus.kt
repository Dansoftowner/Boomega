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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.action.impl.*
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.login.isAutoLoginOn
import com.dansoftware.boomega.gui.login.removeAutoLogin
import com.dansoftware.boomega.gui.login.updateLoginData
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.internalActivityLauncher
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import java.util.concurrent.ExecutorService

/**
 * The base file menu. Supertype of all kind of os specific file-menus.
 */
abstract class BaseFileMenu(private val context: Context) : Menu(i18n("menubar.menu.file")) {

    init {
        this.menuItem(newEntryMenuItem())
            .menuItem(openMenuItem())
            .menuItem(databaseCreatorMenuItem())
            .menuItem(databaseManagerMenuItem())
            .menuItem(recentDatabasesMenuItem())

    }

    /**
     * Menu item that allows the user to show a new entry point (LoginActivity)
     */
    private fun newEntryMenuItem(): MenuItem =
        menuItemOf(NewEntryAction, context)

    /**
     * Menu item that allows the user to open a database file from the file system
     */
    private fun openMenuItem() =
        menuItemOf(OpenDatabaseAction, context)

    private fun databaseCreatorMenuItem() =
        menuItemOf(CreateDatabaseAction, context)

    private fun databaseManagerMenuItem() =
        menuItemOf(OpenDatabaseManagerAction, context)

    /**
     * Menu that allows the user to access the recent databases
     */
    private fun recentDatabasesMenuItem(): MenuItem =
        object : Menu(i18n("menubar.menu.file.recent")) {

            private val recentMenu = this

            private val menuItemFactory: (DatabaseMeta) -> MenuItem = { db ->
                MenuItem(db.toString(), db.provider.icon).also { menuItem ->
                    menuItem.setOnAction {
                        startActivityLauncher {
                            internalActivityLauncher(
                                initialDatabase = db
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
                graphic("database-clock-icon")
                get(DatabaseTracker::class).apply {
                    savedDatabases.forEach { db -> menuItem(menuItemFactory.invoke(db)) }
                    registerObserver(trackerObserver)
                }
            }
        }

    private inline fun startActivityLauncher(crossinline getActivityLauncher: () -> ActivityLauncher) {
        get(ExecutorService::class, "singleThreadExecutor").submit(object : Task<Unit>() {

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

/**
 * The common/general file menu can be used on most operating systems (expect on macOS).
 * It doesn't require a database to be opened (so it's the preferred menu-bar to be applied to the login-view for example).
 *
 * @see MacOsCommonFileMenu
 */
open class CommonFileMenu(context: Context) : BaseFileMenu(context) {
    init {
        separator()
            .menuItem(closeWindowMenuItem(context))
            .menuItem(menuItemOf(RestartApplicationAction, context))
            .menuItem(quitMenuItem())
    }
}

/**
 * The common/general file menu can be used on macOS.
 * It doesn't require a database to be opened (so it's the preferred menu-bar to be applied to the login-view for example).
 *
 * It does not contain the application close & restart items, because that's the role of the [MacOsApplicationMenu].
 *
 * @see CommonFileMenu
 */
open class MacOsCommonFileMenu(context: Context) : BaseFileMenu(context)

/**
 * The file menu should be used with a specific opened database.
 * Can be used on most operating systems (except on macOS).
 *
 * @see MacOsActualFileMenu
 */
open class ActualFileMenu(
    context: Context,
    databaseMeta: DatabaseMeta
) : CommonFileMenu(context) {
    init {
        items.add(5, databaseCloseMenuItem(context, databaseMeta))
        items.add(7, revealInExplorerMenuItem(databaseMeta))
    }
}

/**
 * The file menu should be used with a specific opened database.
 * Should be used on macOS only .
 *
 * @see ActualFileMenu
 */
class MacOsActualFileMenu(context: Context, databaseMeta: DatabaseMeta) : MacOsCommonFileMenu(context) {
    init {
        items.add(5, databaseCloseMenuItem(context, databaseMeta))
        items.add(7, revealInExplorerMenuItem(databaseMeta))
    }
}

/**
 * The menu-item used by the [ActualFileMenu] and the [MacOsActualFileMenu] for closing an opened database.
 */
private fun databaseCloseMenuItem(context: Context, databaseMeta: DatabaseMeta) = MenuItem(i18n("menubar.menu.file.dbclose"))
    .graphic("logout-icon")
    .action {
        get(Preferences::class).updateLoginData {
            if (it.isAutoLoginOn(databaseMeta)) it.removeAutoLogin()
        }
        NewEntryAction.invoke(context)
        context.close()
    }

/**
 * The menu-time used by the [ActualFileMenu] and the [MacOsActualFileMenu] for opening an opened database's location.
 */
private fun revealInExplorerMenuItem(databaseMeta: DatabaseMeta) = MenuItem(i18n("menubar.menu.file.reveal"))
    .graphic("folder-open-icon")
    .apply { isDisable = databaseMeta.isActionSupported(DatabaseMeta.Action.OpenInExternalApplication) }
    .action { databaseMeta.performAction(DatabaseMeta.Action.OpenInExternalApplication) }