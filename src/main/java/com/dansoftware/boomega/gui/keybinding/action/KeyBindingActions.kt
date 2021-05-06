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

package com.dansoftware.boomega.gui.keybinding.action

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.dbcreator.DatabaseCreatorActivity
import com.dansoftware.boomega.gui.dbcreator.DatabaseOpener
import com.dansoftware.boomega.gui.dbmanager.DatabaseManagerActivity
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.keybinding.KeyBinding
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.preferences.PreferencesActivity
import com.dansoftware.boomega.gui.util.typeEquals
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.launcher.ActivityLauncher
import com.dansoftware.boomega.launcher.LauncherMode
import com.dansoftware.boomega.main.ApplicationRestart
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.concurrent.Task
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.input.KeyEvent
import javafx.stage.Stage

/**
 * Encapsulates general actions that are paired with key bindings
 */
class KeyBindingActions(
    private val context: Context,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) {

    fun invoke(action: KeyBindingAction) {
        action.action(context, preferences, databaseTracker)
    }

    fun applyOnScene(scene: Scene) {
        listKeyBindActions().forEach { action ->

            scene.addEventHandler(KeyEvent.KEY_PRESSED) {
                if (action.keyBinding.match(it)) {
                    invoke(action)
                }
            }
        }
    }

    private fun listKeyBindActions(): List<KeyBindingAction> {
        return javaClass.fields.map { it.get(null) }
            .filterIsInstance<KeyBindingAction>()
    }


    companion object {
        @JvmField
        val NEW_ENTRY = KeyBindingAction(KeyBindings.newEntryKeyBinding) { context, preferences, databaseTracker ->
            submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, preferences, databaseTracker))
        }

        @JvmField
        val OPEN_DATABASE =
            KeyBindingAction(KeyBindings.openDatabaseKeyBinding) { context, preferences, databaseTracker ->
                DatabaseOpener().showOpenDialog(context.contextWindow)?.also {
                    // launches the database
                    submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, it, preferences, databaseTracker))
                }
            }

        @JvmField
        val CREATE_DATABASE =
            KeyBindingAction(KeyBindings.createDatabaseKeyBinding) { context, preferences, databaseTracker ->
                DatabaseCreatorActivity().show(databaseTracker, context.contextWindow).ifPresent {
                    // launches the database
                    submitTask(context, ActivityLauncher(LauncherMode.INTERNAL, it, preferences, databaseTracker))
                }
            }

        @JvmField
        val OPEN_DATABASE_MANAGER =
            KeyBindingAction(KeyBindings.openDatabaseManagerKeyBinding) { context, _, databaseTracker ->
                DatabaseManagerActivity().show(databaseTracker, context.contextWindow)
            }

        @JvmField
        val RESTART_APPLICATION = run {
            val dialogShownContexts: MutableSet<Context> = HashSet()
            KeyBindingAction(KeyBindings.restartApplicationKeyBinding) { context, _, _ ->
                if (!dialogShownContexts.contains(context)){
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
            KeyBindingAction(KeyBindings.openSettingsKeyBinding) { context, preferences, _ ->
                PreferencesActivity(preferences).show(context.contextWindow)
            }

        val FULL_SCREEN =
            KeyBindingAction(KeyBindings.fullScreenKeyBinding) { context, _, _ ->
                context.contextWindow.also { if (it is Stage) it.isFullScreen = it.isFullScreen.not() }
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

}