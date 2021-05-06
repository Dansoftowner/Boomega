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
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.info.InformationActivity
import com.dansoftware.boomega.gui.info.contact.ContactActivity
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.keybinding.action.KeyBindingActions
import com.dansoftware.boomega.gui.keybinding.keyBinding
import com.dansoftware.boomega.gui.mainview.MainView
import com.dansoftware.boomega.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.boomega.gui.theme.Theme
import com.dansoftware.boomega.gui.theme.Themeable
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.LauncherMode
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.ReflectionUtils
import com.dansoftware.boomega.util.concurrent.SingleThreadExecutor
import com.dansoftware.boomega.util.revealInExplorer
import com.jfilegoodies.explorer.FileExplorers
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
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class AppMenuBar(context: Context, mainView: MainView, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {

    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(AppMenuBar::class.java)
    }

    private val actions = KeyBindingActions(context, preferences, tracker)
    private lateinit var overlayNotShowing: BooleanBinding

    init {
        initDisablePolicy(mainView)
        this.menus.addAll(
            FileMenu(context, mainView.openedDatabase, preferences, tracker, actions),
            ModuleMenu(mainView),
            PreferencesMenu(context, preferences, actions),
            WindowMenu(context, actions),
            PluginMenu(context, actions),
            HelpMenu(context)
        )
    }

    private fun initDisablePolicy(mainView: MainView) {
        mainView.let {
            this.overlayNotShowing =
                Bindings.isEmpty(it.blockingOverlaysShown).and(Bindings.isEmpty(it.nonBlockingOverlaysShown))
                    .also { observable ->
                        observable.addListener { _, _, isEmpty ->
                            this.isDisable = isEmpty.not()
                        }
                    }
        }
    }

    /**
     * The file menu.
     */
    private class FileMenu(
        val context: Context,
        val databaseMeta: DatabaseMeta,
        val preferences: Preferences,
        val databaseTracker: DatabaseTracker,
        val actions: KeyBindingActions
    ) : Menu(I18N.getValue("menubar.menu.file")) {

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
                .menuItem(restartMenuItem())
                .menuItem(quitMenuItem())
        }

        /**
         * Menu item that allows the user to show a new entry point (LoginActivity)
         */
        private fun newEntryMenuItem(): MenuItem = MenuItem(I18N.getValue("menubar.menu.file.new"))
            .action { actions.invoke(KeyBindingActions.NEW_ENTRY) }
            .keyBinding(KeyBindings.newEntryKeyBinding)
            .graphic(MaterialDesignIcon.DATABASE)

        /**
         * Menu item that allows the user to open a database file from the file system
         */
        private fun openMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.open"))
            .action { actions.invoke(KeyBindingActions.OPEN_DATABASE) }
            .keyBinding(KeyBindings.openDatabaseKeyBinding)
            .graphic(MaterialDesignIcon.FILE)

        private fun databaseCreatorMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.dbcreate"))
            .action { actions.invoke(KeyBindingActions.CREATE_DATABASE) }
            .keyBinding(KeyBindings.createDatabaseKeyBinding)
            .graphic(MaterialDesignIcon.DATABASE_PLUS)

        private fun databaseManagerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.dbmanager"))
            .action { actions.invoke(KeyBindingActions.OPEN_DATABASE_MANAGER) }
            .keyBinding(KeyBindings.openDatabaseManagerKeyBinding)
            .graphic(MaterialDesignIcon.DATABASE)

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

        private fun revealInExplorerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.reveal"))
            .action { databaseMeta.file.revealInExplorer() }
            .graphic(MaterialDesignIcon.FOLDER)

        private fun closeWindowMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.closewindow"))
            .action { context.close() }
            .graphic(MaterialDesignIcon.CLOSE)

        private fun restartMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.restart"))
            .action { actions.invoke(KeyBindingActions.RESTART_APPLICATION) }
            .keyBinding(KeyBindings.restartApplicationKeyBinding)
            .graphic(MaterialDesignIcon.UPDATE)

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

    private class ModuleMenu(val view: MainView) : Menu(I18N.getValue("menubar.menu.modules")) {
        init {
            view.modules.forEach {
                this.menuItem(MenuItem(it.name, it.icon).action { _ -> view.openModule(it) })
            }
        }
    }

    /**
     * The Preferences/Settings menu
     */
    private class PreferencesMenu(val context: Context, val preferences: Preferences, val actions: KeyBindingActions) :
        Menu(I18N.getValue("menubar.menu.preferences")) {
        init {
            this.menuItem(settingsMenu())
                .separator()
                .menuItem(themeMenu())
                .menuItem(langMenu())
        }

        private fun settingsMenu() = MenuItem(I18N.getValue("menubar.menu.preferences.settings"))
            .action { actions.invoke(KeyBindingActions.OPEN_SETTINGS) }
            .keyBinding(KeyBindings.openSettingsKeyBinding)
            .graphic(MaterialDesignIcon.SETTINGS)

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


    /**
     * The 'Window' menu
     */
    private class WindowMenu(val context: Context, val actions: KeyBindingActions) : Menu(I18N.getValue("menubar.menu.window")) {

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

        private fun fullScreenMenuItem() = MenuItem(I18N.getValue("menubar.menu.window.fullscreen"))
            .also { context.contextWindow }
            .action { actions.invoke(KeyBindingActions.FULL_SCREEN) }
            .keyBinding(KeyBindings.fullScreenKeyBinding)
            .graphic(MaterialDesignIcon.FULLSCREEN)
    }

    private class PluginMenu(val context: Context, val actions: KeyBindingActions) : Menu(I18N.getValue("menubar.menu.plugin")) {

        init {
            this.menuItem(pluginManagerMenuItem())
                .menuItem(pluginDirectoryItem())
        }

        private fun pluginManagerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.pluginmanager"))
            .action { PluginManagerActivity().show(context.contextWindow) }
            .graphic(MaterialDesignIcon.POWER_PLUG)

        private fun pluginDirectoryItem() = MenuItem(I18N.getValue("menubar.menu.plugin.opendir"))
            .action { FileExplorers.get().openDir(File(System.getProperty("boomega.plugin.dir"))) }
            .graphic(MaterialDesignIcon.FOLDER)
    }

    private class HelpMenu(val context: Context) : Menu(I18N.getValue("menubar.menu.help")) {

        init {
            this.menuItem(updateSearcherMenuItem())
                .menuItem(contactMenuItem())
                .menuItem(infoMenuItem())
        }

        private fun updateSearcherMenuItem() = MenuItem(I18N.getValue("menubar.menu.help.update"))
            .action { UpdateActivity(context, UpdateSearcher.defaultInstance().search()).show(true) }
            .graphic(MaterialDesignIcon.UPDATE)

        private fun contactMenuItem() = MenuItem(I18N.getValue("menubar.menu.help.contact"))
            .action { ContactActivity(context).show() }
            .graphic(MaterialDesignIcon.CONTACT_MAIL)

        private fun infoMenuItem() = MenuItem(I18N.getValue("menubar.menu.help.about"))
            .action { InformationActivity(context).show() }
            .graphic(MaterialDesignIcon.INFORMATION)
    }
}
