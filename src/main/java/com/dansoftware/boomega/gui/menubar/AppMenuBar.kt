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
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.Themeable
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.LauncherMode
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.util.ReflectionUtils
import com.dansoftware.boomega.util.concurrent.SingleThreadExecutor
import com.dansoftware.boomega.util.revealInExplorer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.ListChangeListener
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.stage.Stage
import javafx.stage.Window
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.*

class AppMenuBar(databaseView: DatabaseView, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {

    private lateinit var overlayNotShowing: BooleanBinding

    init {
        initDisablePolicy(databaseView)
        this.menus.addAll(
            FileMenu(databaseView, databaseView.openedDatabase, preferences, tracker),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView, preferences, tracker),
            ClipboardMenu(databaseView, preferences, tracker),
            WindowMenu(databaseView, preferences, tracker),
            PluginMenu(databaseView, preferences, tracker),
            HelpMenu(databaseView, preferences, tracker)
        )
    }

    private fun initDisablePolicy(databaseView: DatabaseView) {
        databaseView.let {
            this.overlayNotShowing =
                Bindings.isEmpty(it.blockingOverlaysShown).and(Bindings.isEmpty(it.nonBlockingOverlaysShown))
                    .also { observable ->
                        observable.addListener { _, _, isEmpty ->
                            this.isDisable = isEmpty.not()
                        }
                    }
        }
    }

    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(AppMenuBar::class.java)
    }

    /**
     * The file menu.
     */
    private class FileMenu(
        val context: Context,
        val databaseMeta: DatabaseMeta,
        val preferences: Preferences,
        val databaseTracker: DatabaseTracker
    ) : Menu(I18N.getValue("menubar.menu.file")) {

        init {
            this.menuItem(newEntryMenuItem())
                .menuItem(openMenuItem())
                .menuItem(databaseCreatorMenuItem())
                .menuItem(databaseManagerMenuItem())
                .menuItem(recentDatabasesMenuItem())
                .menuItem(databaseCloseMenuItem())
                .separator()
                .menuItem(revealInExplorerMenuItem())
                .separator()
                .menuItem(closeWindowMenuItem())
                .menuItem(restartMenuItem())
                .menuItem(quitMenuItem())
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
            object : Menu(I18N.getValue("menubar.menu.file.recent")) {
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
                    this.graphic(MaterialDesignIcon.BOOK_OPEN_VARIANT)
                }
            }

        private fun databaseCloseMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.dbclose"))
            .action {
                preferences.editor()
                    .put(PreferenceKey.LOGIN_DATA, preferences.get(PreferenceKey.LOGIN_DATA).apply {
                        if (autoLoginDatabase.equals(databaseMeta)) {
                            isAutoLogin = false
                            autoLoginCredentials = null
                        }
                    }).tryCommit()
                context.close()
                GlobalActions.NEW_ENTRY.invoke(context, preferences, databaseTracker)
            }
            .graphic(MaterialDesignIcon.LOGOUT_VARIANT)

        private fun revealInExplorerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.reveal"))
            .action { databaseMeta.file.revealInExplorer() }
            .graphic(MaterialDesignIcon.FOLDER)

        private fun closeWindowMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.closewindow"))
            .action { context.close() }
            .graphic(MaterialDesignIcon.CLOSE)

        private fun restartMenuItem() =
            MenuItems.of(GlobalActions.RESTART_APPLICATION, context, preferences, databaseTracker)

        private fun quitMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.quit"))
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
                }
            })
        }
    }

    private class ModuleMenu(val view: DatabaseView) : Menu(I18N.getValue("menubar.menu.modules")) {
        init {
            view.modules.forEach {
                this.menuItem(MenuItem(it.name, it.icon).action { _ -> view.openModule(it) })
            }
        }
    }

    /**
     * The Preferences/Settings menu
     */
    private class PreferencesMenu(
        val context: Context,
        val preferences: Preferences,
        val databaseTracker: DatabaseTracker
    ) : Menu(I18N.getValue("menubar.menu.preferences")) {
        init {
            this.menuItem(settingsMenu())
                .separator()
                .menuItem(themeMenu())
                .menuItem(langMenu())
        }

        private fun settingsMenu() =
            MenuItems.of(GlobalActions.OPEN_SETTINGS, context, preferences, databaseTracker)

        private fun themeMenu() = object : Menu(I18N.getValue("menubar.menu.preferences.theme")) {

            private val themeChangeListener = Themeable { _, newTheme ->
                items.forEach { if (it is RadioMenuItem) it.isSelected = newTheme.javaClass == it.userData }
            }

            init {
                Theme.registerThemeable(themeChangeListener)
                this.graphic(MaterialDesignIcon.FORMAT_PAINT)
                this.buildItems()
            }

            private fun buildItems() {
                val toggleGroup = ToggleGroup()
                Theme.getAvailableThemesData().forEach { themeMeta ->
                    this.menuItem(RadioMenuItem(themeMeta.displayName).also {
                        it.toggleGroup = toggleGroup
                        it.userData = themeMeta.themeClass
                        it.isSelected = Theme.getDefault().javaClass == themeMeta.themeClass
                        it.action {
                            try {
                                val themeObject = ReflectionUtils.constructObject(themeMeta.themeClass)
                                logger.debug("The theme object: {}", themeObject)
                                Theme.setDefault(themeObject)
                                preferences.editor().put(PreferenceKey.THEME, themeObject)
                            } catch (e: Exception) {
                                logger.error("Couldn't set the theme", e)
                                // TODO: error dialog
                            }
                        }
                    })
                }
            }
        }

        private fun langMenu() = Menu(I18N.getValue("menubar.menu.preferences.lang"))
            .also { menu ->
                val toggleGroup = ToggleGroup()
                I18N.getAvailableLocales().forEach { locale ->
                    menu.menuItem(RadioMenuItem(locale.displayLanguage).also {
                        it.toggleGroup = toggleGroup
                        it.isSelected = Locale.getDefault() == locale
                        it.setOnAction {
                            preferences.editor().put(PreferenceKey.LOCALE, locale)
                            context.showConfirmationDialog(
                                I18N.getValue("app.lang.restart.title"),
                                I18N.getValue("app.lang.restart.msg")
                            ) { btn ->
                                when {
                                    btn.typeEquals(ButtonType.YES) -> ApplicationRestart().restartApp()
                                }
                            }
                        }
                    })
                }
            }
            .graphic(MaterialDesignIcon.TRANSLATE)
    }


    private class ClipboardMenu(
        val context: Context,
        val preferences: Preferences,
        val databaseTracker: DatabaseTracker
    ) : Menu(I18N.getValue("menubar.menu.clipboard")) {
        init {
            this.menuItem(clipboardViewItem())
        }

        private fun clipboardViewItem() =
            MenuItems.of(GlobalActions.OPEN_CLIPBOARD_VIEWER, context, preferences, databaseTracker)
    }

    /**
     * The 'Window' menu
     */
    private class WindowMenu(val context: Context, val preferences: Preferences, val databaseTracker: DatabaseTracker) :
        Menu(I18N.getValue("menubar.menu.window")) {

        private val windowsChangeOperator = object {
            fun onWindowsAdded(windows: List<Window>) {
                windows.filter { it is Stage && it.owner == null }.map { it as Stage }.forEach { window ->
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
                windows.filter { it is Stage && it.owner == null }.forEach { window ->
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

        private class WeakWindowsChangeListener(val weakReference: WeakReference<ListChangeListener<Window>>) :
            ListChangeListener<Window> {

            init {
                Window.getWindows().addListener(this)
            }

            override fun onChanged(c: ListChangeListener.Change<out Window>?) {
                weakReference.get()?.onChanged(c) ?: Window.getWindows().removeListener(this)
            }
        }

        init {
            this.menuItem(fullScreenMenuItem()).separator()
            windowsChangeOperator.onWindowsAdded(Window.getWindows())
            WeakWindowsChangeListener(WeakReference(windowListChangeListener))
        }

        private fun fullScreenMenuItem() =
            MenuItems.of(GlobalActions.FULL_SCREEN, context, preferences, databaseTracker, ::CheckMenuItem)
                .apply {
                    context.onWindowPresent { window ->
                        if (window is Stage)
                            window.fullScreenProperty().addListener { _, _, isFullScreen ->
                                selectedProperty().set(isFullScreen)
                            }
                    }
                }
    }

    private class PluginMenu(val context: Context, val preferences: Preferences, val databaseTracker: DatabaseTracker) :
        Menu(I18N.getValue("menubar.menu.plugin")) {

        init {
            this.menuItem(pluginManagerMenuItem())
                .menuItem(pluginDirectoryItem())
        }

        private fun pluginManagerMenuItem() =
            MenuItems.of(GlobalActions.OPEN_PLUGIN_MANAGER, context, preferences, databaseTracker).apply {
                isDisable = true // TODO: unlock plugin manager
            }

        private fun pluginDirectoryItem() =
            MenuItems.of(GlobalActions.OPEN_PLUGIN_DIR, context, preferences, databaseTracker)
    }

    private class HelpMenu(val context: Context, val preferences: Preferences, val databaseTracker: DatabaseTracker) :
        Menu(I18N.getValue("menubar.menu.help")) {

        init {
            this.menuItem(updateSearcherMenuItem())
                .menuItem(contactMenuItem())
                .menuItem(infoMenuItem())
        }

        private fun updateSearcherMenuItem() =
            MenuItems.of(GlobalActions.SEARCH_FOR_UPDATES, context, preferences, databaseTracker)

        private fun contactMenuItem() =
            MenuItems.of(GlobalActions.OPEN_CONTACT_INFO, context, preferences, databaseTracker)

        private fun infoMenuItem() =
            MenuItems.of(GlobalActions.OPEN_APP_INFO, context, preferences, databaseTracker)
    }
}
