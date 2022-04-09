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
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.internalActivityLauncher
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import java.util.concurrent.ExecutorService

/**
 * The base file menu. Supertype of all kind of os specific file-menus.
 */
@Suppress("LeakingThis")
abstract class BaseFileMenu(private val context: Context, openedDatabase: DatabaseMeta?) : Menu(i18n("menubar.menu.file")) {

    init {
        addSectionItems(firstSectionItems(context, openedDatabase), true)
        addSectionItems(secondSectionItems(context, openedDatabase), true)
        addSectionItems(thirdSectionItems(context, openedDatabase), false)
    }

    private fun addSectionItems(sectionItems: List<MenuItem>, separator: Boolean) {
        items.addAll(sectionItems)
        if (separator && sectionItems.isNotEmpty()) items.add(SeparatorMenuItem())
    }

    /**
     * Returns the list of menu-items should be placed in the first section
     */
    open fun firstSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = listOf(
        menuItemOf(NewEntryAction, context),
        menuItemOf(OpenDatabaseAction, context),
        menuItemOf(CreateDatabaseAction, context),
        menuItemOf(OpenDatabaseManagerAction, context),
        recentDatabasesMenuItem()
    )

    /**
     * Returns the list of menu-items should be placed in the second section
     */
    open fun secondSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = listOf(
        databaseCloseMenuItem(context, openedDatabase!!),
        revealInExplorerMenuItem(openedDatabase)
    )

    /**
     * Returns the list of menu-items should be placed in the third section
     */
    open fun thirdSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = listOf(
        menuItemOf(CloseWindowAction, context),
        menuItemOf(RestartApplicationAction, context),
        menuItemOf(QuitAppAction, context)
    )

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

    private fun databaseCloseMenuItem(context: Context, databaseMeta: DatabaseMeta) =
        MenuItem(i18n("menubar.menu.file.dbclose"))
            .graphic("logout-icon")
            .action {
                get(com.dansoftware.boomega.config.Preferences::class).updateLoginData {
                    if (it.isAutoLoginOn(databaseMeta)) it.removeAutoLogin()
                }
                NewEntryAction.invoke(context)
                context.close()
            }

    private fun revealInExplorerMenuItem(databaseMeta: DatabaseMeta) = MenuItem(i18n("menubar.menu.file.reveal"))
        .graphic("folder-open-icon")
        .apply { isDisable = databaseMeta.isActionSupported(DatabaseMeta.Action.OpenInExternalApplication) }
        .action { databaseMeta.performAction(DatabaseMeta.Action.OpenInExternalApplication) }

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
open class CommonFileMenu(context: Context) : BaseFileMenu(context, null) {
    override fun secondSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = emptyList()
}

/**
 * The common/general file menu can be used on macOS.
 * It doesn't require a database to be opened (so it's the preferred menu-bar to be applied to the login-view for example).
 *
 * It does not contain the application close & restart items, because that's the role of the [MacOsApplicationMenu].
 *
 * @see CommonFileMenu
 */
open class MacOsCommonFileMenu(context: Context) : BaseFileMenu(context, null) {
    override fun secondSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = emptyList()
    override fun thirdSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = emptyList()
}

/**
 * The file menu should be used with a specific opened database.
 * Can be used on most operating systems (except on macOS).
 *
 * @see MacOsFullFileMenu
 */
open class FullFileMenu(context: Context, openedDatabase: DatabaseMeta) : BaseFileMenu(context, openedDatabase)

/**
 * The file menu should be used with a specific opened database.
 * Should be used on macOS only .
 *
 * @see FullFileMenu
 */
class MacOsFullFileMenu(context: Context, openedDatabase: DatabaseMeta) : BaseFileMenu(context, openedDatabase) {
    override fun thirdSectionItems(context: Context, openedDatabase: DatabaseMeta?): List<MenuItem> = emptyList()
}