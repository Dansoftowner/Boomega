package com.dansoftware.libraryapp.gui.mainview

import com.dansoftware.libraryapp.appdata.Preferences
import com.dansoftware.libraryapp.appdata.logindata.LoginData
import com.dansoftware.libraryapp.db.DatabaseMeta
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.gui.entry.DatabaseTracker
import com.dansoftware.libraryapp.gui.util.action
import com.dansoftware.libraryapp.gui.util.menuItem
import com.dansoftware.libraryapp.launcher.ActivityLauncher
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.SingleThreadExecutor
import javafx.concurrent.Task
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem

class MenuBar(context: Context, preferences: Preferences, tracker: DatabaseTracker) : javafx.scene.control.MenuBar() {
    init {
        this.menus.addAll(
            FileMenu(context, preferences, tracker)
        )
    }

    private class FileMenu(val context: Context, val preferences: Preferences, val databaseTracker: DatabaseTracker) :
        Menu(I18N.getMenuBarValue("menubar.menu.file")) {

        init {
            this.items.addAll(
                MenuItem(I18N.getMenuBarValue("menubar.menu.file.new")).action { newLibraryAppEntry() },
                SeparatorMenuItem(),
                Menu(I18N.getMenuBarValue("menubar.menu.file.recent")).also {
                    databaseTracker.savedDatabases.forEach { db ->
                        it.menuItem(MenuItem(db.toString()).also {
                            it.setOnAction {
                                //
                            }
                        })
                    }
                }
            )
        }

        private fun newLibraryAppEntry() {
            SingleThreadExecutor.submit(Thread(object : Task<Unit>() {
                init {
                    this.setOnRunning { context.showIndeterminateProgress() }
                    this.setOnFailed { context.stopProgress() }
                    this.setOnSucceeded { context.stopProgress() }
                }

                override fun call() {
                    RuntimeBasicActivityLauncher(preferences, databaseTracker).launch()
                }
            }))
        }
    }
}

private class RuntimeOpenActivityLauncher(
    preferences: Preferences,
    databaseTracker: DatabaseTracker,
    databaseMeta: DatabaseMeta
) {
    init {

    }
}

private class RuntimeBasicActivityLauncher(preferences: Preferences, databaseTracker: DatabaseTracker) :
    ActivityLauncher(preferences, databaseTracker) {

    private var loginData: LoginData

    init {
        loginData = preferences.get(Preferences.Key.LOGIN_DATA).also {
            it.selectedDatabase = null
            it.isAutoLogin = false
        }
    }

    override fun getLoginData() = loginData

    override fun saveLoginData(loginData: LoginData?) {
        this.loginData = loginData!!
    }

    override fun onActivityLaunched(context: Context) {
    }
}
