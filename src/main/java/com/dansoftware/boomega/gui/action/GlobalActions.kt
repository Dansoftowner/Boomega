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

package com.dansoftware.boomega.gui.action

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.clipboard.ClipboardViewActivity
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbcreator.DatabaseOpener
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.info.InformationActivity
import com.dansoftware.boomega.gui.info.contact.ContactActivity
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.pluginmngr.PluginManagerActivity
import com.dansoftware.boomega.gui.preferences.PreferencesActivity
import com.dansoftware.boomega.gui.updatedialog.UpdateActivity
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.LauncherMode
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.update.UpdateSearcher
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import com.jfilegoodies.explorer.FileExplorers
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.concurrent.Task
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import java.io.File

object GlobalActions {

    /* --------------------------------------------------------------------> */

    @JvmField
    val NEW_ENTRY = Action(
        "action.new_entry",
        MaterialDesignIcon.DATABASE,
        KeyBindings.newEntryKeyBinding
    ) { context, preferences, databaseTracker ->
        submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, preferences, databaseTracker))
    }

    @JvmField
    val OPEN_DATABASE =
        Action(
            "action.open_database",
            MaterialDesignIcon.FILE,
            KeyBindings.openDatabaseKeyBinding
        ) { context, preferences, databaseTracker ->
            DatabaseOpener().showOpenDialog(context.contextWindow)?.also {
                // launches the database
                submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, it, preferences, databaseTracker))
            }
        }

    @JvmField
    val CREATE_DATABASE =
        Action(
            "action.create_database",
            MaterialDesignIcon.DATABASE_PLUS,
            KeyBindings.createDatabaseKeyBinding
        ) { context, preferences, databaseTracker ->
            DatabaseCreatorActivity().show(databaseTracker, context.contextWindow).ifPresent {
                // launches the database
                submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, it, preferences, databaseTracker))
            }
        }

    @JvmField
    val OPEN_DATABASE_MANAGER =
        Action(
            "action.open_database_manager",
            MaterialDesignIcon.DATABASE,
            KeyBindings.openDatabaseManagerKeyBinding
        ) { context, _, databaseTracker ->
            DatabaseManagerActivity().show(databaseTracker, context.contextWindow)
        }

    @JvmField
    val RESTART_APPLICATION = run {
        val dialogShownContexts: MutableSet<Context> = HashSet()
        Action(
            "action.restart",
            MaterialDesignIcon.UPDATE,
            KeyBindings.restartApplicationKeyBinding
        ) { context, _, _ ->
            if (!dialogShownContexts.contains(context)) {
                context.showConfirmationDialog(
                    I18N.getValue("app.restart.dialog.title"),
                    I18N.getValue("app.restart.dialog.msg")
                ) {
                    when {
                        it.typeEquals(ButtonType.YES) -> ApplicationRestart().restartApp()
                    }
                    dialogShownContexts.remove(context)
                }
                dialogShownContexts.add(context)
            }
        }
    }

    @JvmField
    val OPEN_SETTINGS =
        Action(
            "action.settings",
            MaterialDesignIcon.SETTINGS,
            KeyBindings.openSettingsKeyBinding
        ) { context, preferences, _ ->
            PreferencesActivity(preferences).show(context.contextWindow)
        }

    @JvmField
    val FULL_SCREEN =
        Action(
            "action.full_screen",
            MaterialDesignIcon.FULLSCREEN,
            KeyBindings.fullScreenKeyBinding
        ) { context, _, _ ->
            context.contextWindow.also { if (it is Stage) it.isFullScreen = it.isFullScreen.not() }
        }

    @JvmField
    val OPEN_CLIPBOARD_VIEWER =
        Action("action.open_clipboard_view", MaterialDesignIcon.CLIPBOARD) { context, _, _ ->
            ClipboardViewActivity.show(context.contextWindow)
        }

    @JvmField
    val OPEN_PLUGIN_MANAGER =
        Action("action.open_plugin_manager", MaterialDesignIcon.POWER_PLUG) { context, _, _ ->
            PluginManagerActivity().show(context.contextWindow)
        }

    @JvmField
    val OPEN_PLUGIN_DIR =
        Action("action.open_plugin_dir", MaterialDesignIcon.FOLDER) { context, _, _ ->
            FileExplorers.get().openDir(File(System.getProperty("boomega.plugin.dir")))
        }

    @JvmField
    val SEARCH_FOR_UPDATES =
        Action("action.update_search", MaterialDesignIcon.UPDATE) { context, _, _ ->
            UpdateActivity(context, UpdateSearcher.defaultInstance().search()).show(true)
        }

    @JvmField
    val OPEN_CONTACT_INFO =
        Action("action.open_contact_info", MaterialDesignIcon.CONTACT_MAIL) { context, _, _ ->
            ContactActivity(context).show()
        }

    @JvmField
    val OPEN_APP_INFO =
        Action("action.open_app_info", MaterialDesignIcon.INFORMATION) { context, _, _ ->
            InformationActivity(context).show()
        }

    /* <-------------------------------------------------------------------- */

    val allActions: List<Action> = this.javaClass.declaredFields
        .filter { Action::class.java.isAssignableFrom(it.type) }
        .mapNotNull { it.get(null) as Action? }

    /**
     * Applies the key-binding actions on the given scene.
     */
    fun applyOnScene(
        scene: Scene,
        context: Context,
        preferences: Preferences,
        databaseTracker: DatabaseTracker
    ) {
        listKeyBindActions().forEach { action ->
            scene.addEventHandler(KeyEvent.KEY_PRESSED) {
                if (action.keyBinding!!.match(it)) {
                    action.invoke(context, preferences, databaseTracker)
                }
            }
        }
    }

    private fun listKeyBindActions(): List<Action> {
        return allActions.filter { it.keyBinding !== null }
    }

    private fun submitTask(context: Context, runnable: Runnable) {
        CachedExecutor.submit(object : Task<Unit>() {
            init {
                this.setOnRunning { context.showIndeterminateProgress() }
                this.setOnFailed { context.stopProgress() }
                this.setOnSucceeded { context.stopProgress() }
            }

            override fun call() {
                runnable.run()
            }
        })
    }

}