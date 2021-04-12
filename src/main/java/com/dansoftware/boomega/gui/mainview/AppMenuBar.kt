package com.dansoftware.boomega.gui.mainview

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbcreator.DatabaseOpener
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.info.InformationActivity
import com.dansoftware.boomega.gui.info.contact.ContactActivity
import com.dansoftware.boomega.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.boomega.gui.preferences.PreferencesActivity
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
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.collections.ListChangeListener
import javafx.concurrent.Task
import javafx.scene.control.*
import javafx.scene.input.KeyCodeCombination
import javafx.stage.Stage
import javafx.stage.Window
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import java.util.*

class AppMenuBar(context: Context, mainView: MainView, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {

    companion object {
        @JvmStatic
        val logger: Logger = LoggerFactory.getLogger(AppMenuBar::class.java)
    }

    private lateinit var overlayNotShowing: BooleanBinding

    init {
        initDisablePolicy(mainView)
        this.menus.addAll(
            FileMenu(context, mainView.openedDatabase, preferences, tracker),
            ModuleMenu(mainView),
            PreferencesMenu(context, preferences),
            WindowMenu(context),
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
        val databaseTracker: DatabaseTracker
    ) : Menu(I18N.getValue("menubar.menu.file")) {

        init {
            this.menuItem(newEntryMenuItem())
                .menuItem(openMenuItem())
                .menuItem(databaseCreatorMenuItem())
                .menuItem(databaseManagerMenuItem())
                .menuItem(recentDatabasesMenuItem())
                .separator()
                .menuItem(pluginManagerMenuItem())
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
            .action { startActivityLauncher { RuntimeBasicActivityLauncher(preferences, databaseTracker) } }
            .keyCombination(KeyBindings.newEntryKeyBinding.keyCombinationProperty)
            .graphic(MaterialDesignIcon.DATABASE)

        /**
         * Menu item that allows the user to open a database file from the file system
         */
        private fun openMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.open"))
            .action {
                DatabaseOpener().showOpenDialog(context.contextWindow)?.also {
                    startActivityLauncher {
                        RuntimeOpenActivityLauncher(
                            preferences,
                            databaseTracker,
                            it
                        )
                    }
                }
            }
            .keyCombination(KeyBindings.openDatabaseKeyBinding.keyCombinationProperty)
            .graphic(MaterialDesignIcon.FILE)

        private fun databaseCreatorMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.dbcreate"))
            .action {
                DatabaseCreatorActivity().show(databaseTracker, context.contextWindow).ifPresent { db ->
                    startActivityLauncher { RuntimeOpenActivityLauncher(preferences, databaseTracker, db) }
                }
            }
            .keyCombination(KeyBindings.createDatabaseKeyBinding.keyCombinationProperty)
            .graphic(MaterialDesignIcon.DATABASE_PLUS)

        private fun databaseManagerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.dbmanager"))
            .action { DatabaseManagerActivity().show(databaseTracker, context.contextWindow) }
            .keyCombination(KeyBindings.openDatabaseKeyBinding.keyCombinationProperty)
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

        private fun pluginManagerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.pluginmanager"))
            .action { PluginManagerActivity().show(context.contextWindow) }
            .graphic(MaterialDesignIcon.POWER_PLUG)

        private fun revealInExplorerMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.reveal"))
            .action { databaseMeta.file.revealInExplorer() }
            .graphic(MaterialDesignIcon.FOLDER)

        private fun closeWindowMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.closewindow"))
            .action { context.close() }
            .graphic(MaterialDesignIcon.CLOSE)

        private fun restartMenuItem() = MenuItem(I18N.getValue("menubar.menu.file.restart"))
            .action {
                context.contextScene
                    ?.onKeyPressed
                    ?.handle(
                        KeyBindings.restartApplicationKeyBinding
                        .keyCombination.let { it as KeyCodeCombination }
                        .asKeyEvent()
                    )
            }
            .keyCombination(KeyBindings.restartApplicationKeyBinding.keyCombinationProperty)
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
                    databaseTracker.savedDatabases.forEach { println(it) }
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
    private class PreferencesMenu(val context: Context, val preferences: Preferences) :
        Menu(I18N.getValue("menubar.menu.preferences")) {
        init {
            this.menuItem(settingsMenu())
                .separator()
                .menuItem(themeMenu())
                .menuItem(langMenu())
        }

        private fun settingsMenu() = MenuItem(I18N.getValue("menubar.menu.preferences.settings"))
            .action { PreferencesActivity(preferences).show(context.contextWindow) }
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
                                preferences.editor().put(Preferences.Key.THEME, themeObject)
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
                            preferences.editor().put(Preferences.Key.LOCALE, locale)
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
    private class WindowMenu(val context: Context) : Menu(I18N.getValue("menubar.menu.window")) {

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
            .action { context.contextWindow.also { if (it is Stage) it.isFullScreen = it.isFullScreen.not() } }
            .keyCombination(KeyBindings.fullScreenKeyBinding.keyCombinationProperty)
            .graphic(MaterialDesignIcon.FULLSCREEN)
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
