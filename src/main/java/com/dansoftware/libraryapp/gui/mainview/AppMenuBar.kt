package com.dansoftware.libraryapp.gui.mainview

import com.dansoftware.libraryapp.appdata.Preferences
import com.dansoftware.libraryapp.db.DatabaseMeta
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.libraryapp.gui.dbcreator.DatabaseOpener
import com.dansoftware.libraryapp.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker
import com.dansoftware.libraryapp.gui.entry.DefaultKeyBindings
import com.dansoftware.libraryapp.gui.util.*
import com.dansoftware.libraryapp.launcher.ActivityLauncher
import com.dansoftware.libraryapp.launcher.LauncherMode
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.SingleThreadExecutor
import com.dansoftware.libraryapp.util.revealInExplorer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.concurrent.Task
import javafx.scene.control.CheckMenuItem
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import javafx.stage.Window
import java.lang.ref.WeakReference

class AppMenuBar(context: Context, databaseMeta: DatabaseMeta, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {
    init {
        this.menus.addAll(
            FileMenu(context, databaseMeta, preferences, tracker),
            WindowMenu(context)
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
            this.menuItem(newEntryMenuItem())
                .menuItem(openMenuItem())
                .menuItem(databaseCreatorMenuItem())
                .menuItem(databaseManagerMenuItem())
                .menuItem(recentDatabasesMenuItem())
                .separator()
                .menuItem(revealInExplorerMenuItem())
                .separator()
                .menuItem(closeWindowMenuItem())
                .separator()
                .menuItem(restartMenuItem())
                .menuItem(quitMenuItem())
        }

        /**
         * Menu item that allows the user to show a new entry point (LoginActivity)
         */
        private fun newEntryMenuItem(): MenuItem = MenuItem(I18N.getMenuBarValue("menubar.menu.file.new"))
            .action { startActivityLauncher { RuntimeBasicActivityLauncher(preferences, databaseTracker) } }
            .keyCombination(DefaultKeyBindings.NEW_ENTRY)
            .graphic(MaterialDesignIcon.DATABASE)

        /**
         * Menu item that allows the user to open a database file from the file system
         */
        private fun openMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.open"))
            .action {
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
            }
            .keyCombination(DefaultKeyBindings.OPEN_DATABASE)
            .graphic(MaterialDesignIcon.FILE)

        private fun databaseCreatorMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.dbcreate"))
            .action {
                DatabaseCreatorActivity().show(databaseTracker, context.contextWindow).ifPresent { db ->
                    startActivityLauncher { RuntimeOpenActivityLauncher(preferences, databaseTracker, db) }
                }
            }
            .keyCombination(DefaultKeyBindings.CREATE_DATABASE)
            .graphic(MaterialDesignIcon.DATABASE_PLUS)

        private fun databaseManagerMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.dbmanager"))
            .action { DatabaseManagerActivity().show(databaseTracker, context.contextWindow) }
            .keyCombination(DefaultKeyBindings.OPEN_DATABASE_MANAGER)
            .graphic(MaterialDesignIcon.DATABASE)

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
                    this.graphic(MaterialDesignIcon.BOOK_OPEN_VARIANT)
                }
            }

        private fun revealInExplorerMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.reveal"))
            .action { databaseMeta.file.revealInExplorer() }
            .graphic(MaterialDesignIcon.FOLDER)

        private fun closeWindowMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.closewindow"))
            .action { context.close() }
            .graphic(MaterialDesignIcon.CLOSE)

        private fun restartMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.restart"))
            .action { context.contextScene?.onKeyPressed?.handle(DefaultKeyBindings.RESTART_APPLICATION.asKeyEvent()) }
            .keyCombination(DefaultKeyBindings.RESTART_APPLICATION)
            .graphic(MaterialDesignIcon.UPDATE)

        private fun quitMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.file.quit"))
            .action { Platform.exit() }
            .graphic(MaterialDesignIcon.CLOSE_BOX)

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

    /**
     * The 'Window' menu
     */
    private class WindowMenu(val context: Context) :
        Menu(I18N.getMenuBarValue("menubar.menu.window")) {

        private val windowsChangeOperator = object {
            fun onWindowsAdded(windows: List<Window>) {
                windows.filter { it is Stage && it.owner == null} .map { it as Stage }.forEach { window ->
                    this@WindowMenu.menuItem(CheckMenuItem().also {
                        it.userData = WeakReference<Window>(window)
                        it.textProperty().bind(window.titleProperty())
                        window.focusedProperty().addListener { _, _, yes ->
                            it.isSelected = yes
                        }
                        it.setOnAction { window.toFront() }
                    })
                }
            }

            fun onWindowsRemoved(windows: List<Window>) {
                windows.filter { it is Stage && it.owner == null}.forEach { window ->
                    val iterator = this@WindowMenu.items.iterator()
                    while (iterator.hasNext()) {
                        val element = iterator.next()
                        if (element.userData is WeakReference<*>) {
                            when {
                                element.userData === null || (element.userData as WeakReference<*>).get() == window ->
                                    iterator.remove()
                            }
                        }
                    }
                }
            }
        }

        private val windowListChangeListener = ListChangeListener<Window> { change ->
            while (change.next()) {
                when {
                    change.wasAdded() -> windowsChangeOperator.onWindowsAdded(change.addedSubList)
                    change.wasRemoved() -> windowsChangeOperator.onWindowsRemoved(change.removed)
                }
            }
        }

        init {
            this.menuItem(fullScreenMenuItem()).separator()
            windowsChangeOperator.onWindowsAdded(Window.getWindows())
            Window.getWindows().addListener(windowListChangeListener)
        }

        private fun fullScreenMenuItem() = MenuItem(I18N.getMenuBarValue("menubar.menu.window.fullscreen"))
            .also { context.contextWindow }
            .action { context.contextWindow.also { if (it is Stage) it.isFullScreen = it.isFullScreen.not() } }
            .keyCombination(DefaultKeyBindings.FULL_SCREEN)
            .graphic(MaterialDesignIcon.FULLSCREEN)
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
