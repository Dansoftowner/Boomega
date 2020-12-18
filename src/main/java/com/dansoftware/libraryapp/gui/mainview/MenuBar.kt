package com.dansoftware.libraryapp.gui.mainview

import com.dansoftware.libraryapp.appdata.Preferences
import com.dansoftware.libraryapp.db.DatabaseMeta
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseOpener
import com.dansoftware.libraryapp.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker
import com.dansoftware.libraryapp.gui.util.action
import com.dansoftware.libraryapp.gui.util.keyCombination
import com.dansoftware.libraryapp.gui.util.menuItem
import com.dansoftware.libraryapp.launcher.ActivityLauncher
import com.dansoftware.libraryapp.launcher.LauncherMode
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.SingleThreadExecutor
import com.dansoftware.libraryapp.util.revealInExplorer
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination

class MenuBar(context: Context, databaseMeta: DatabaseMeta, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {
    init {
        this.menus.addAll(
            FileMenu(context, databaseMeta, preferences, tracker)
        )
    }

    /**
     * The file menu.
     */
    private class FileMenu(
        val context: Context,
        val databaseMeta: DatabaseMeta,
        val preferences: Preferences,
        val databaseTracker: DatabaseTracker
    ) : Menu(I18N.getMenuBarValue("menubar.menu.file")) {

        init {
            this.items.addAll(
                newEntryMenuItem(),
                openMenuItem(),
                databaseManagerMenuItem(),
                recentDatabasesMenuItem(),
                SeparatorMenuItem(),
                revealInExplorerMenuItem(),
                SeparatorMenuItem(),
                closeWindowMenuItem(),
                SeparatorMenuItem(),
                quitMenuItem()
            )
        }

        /**
         * Menu item that allows the user to show a new entry point (LoginActivity)
         */
        private fun newEntryMenuItem(): MenuItem = MenuItem(I18N.getMenuBarValue("menubar.menu.file.new")).action {
            startActivityLauncher { RuntimeBasicActivityLauncher(preferences, databaseTracker) }
        }.keyCombination(KeyCodeCombination(KeyCode.N, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN))

        /**
         * Menu item that allows the user to open a database file from the file system
         */
        private fun openMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.open")).action {
            DatabaseOpener().showOpenDialog(context.contextWindow)?.also {
                startActivityLauncher {
                    ActivityLauncher(
                        LauncherMode.ALREADY_RUNNING,
                        it,
                        preferences,
                        databaseTracker
                    )
                }
            }
        }.keyCombination(KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN))

        /**
         * Menu that allows the user to access the recent databases
         */
        private fun recentDatabasesMenuItem(): MenuItem =
            object : Menu(I18N.getMenuBarValue("menubar.menu.file.recent")) {
                private val it = this
                private val menuItemFactory: (DatabaseMeta) -> MenuItem = { db ->
                    MenuItem(db.toString()).also { menuItem ->
                        menuItem.setOnAction {
                            startActivityLauncher { RuntimeOpenActivityLauncher(preferences, databaseTracker, db) }
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
                }
            }

        private fun revealInExplorerMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.reveal")).action {
            databaseMeta.file.revealInExplorer()
        }

        private fun databaseManagerMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.dbmanager")).action {
            DatabaseManagerActivity().show(databaseTracker, context.contextWindow)
        }.keyCombination(KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN))

        private fun closeWindowMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.closewindow")).action {
            context.close()
        }

        private fun quitMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.quit")).action { Platform.exit() }

        private fun startActivityLauncher(getActivityLauncher: () -> ActivityLauncher) {
            SingleThreadExecutor.submit(object : Task<Unit>() {
                init {
                    this.setOnRunning { context.showIndeterminateProgress() }
                    this.setOnFailed { context.stopProgress() }
                    this.setOnSucceeded { context.stopProgress() }
                }

                override fun call() {
                    getActivityLauncher().launch()
                    databaseTracker.savedDatabases.forEach { println(it) }
                }
            })
        }
    }
}

/**
 * Used for launching a database at runtime
 */
private class RuntimeOpenActivityLauncher(
    preferences: Preferences,
    tracker: DatabaseTracker,
    databaseMeta: DatabaseMeta,
) : ActivityLauncher(LauncherMode.INTERNAL, databaseMeta, preferences, tracker)

/**
 * Used for launching an EntryActivity at runtime
 */
private class RuntimeBasicActivityLauncher(preferences: Preferences, databaseTracker: DatabaseTracker) :
    ActivityLauncher(LauncherMode.INTERNAL, preferences, databaseTracker)
