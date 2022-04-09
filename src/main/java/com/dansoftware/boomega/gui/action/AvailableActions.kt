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

package com.dansoftware.boomega.gui.action

import com.dansoftware.boomega.gui.action.impl.*
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.util.toImmutableList
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import java.util.*

object AvailableActions : List<Action> by LinkedList(loadActions()).toImmutableList() {

    private val keyBindActions get() = filter { it.keyBinding !== null }

    fun applyOnScene(scene: Scene, context: Context) {
        keyBindActions.forEach { action ->
            scene.addEventHandler(KeyEvent.KEY_PRESSED) {
                if (action.keyBinding!!.match(it)) {
                    action.invoke(context)
                }
            }
        }
    }
}

private fun loadActions() = loadBuiltInActions().plus(loadActionsFromPlugins()).toList()

private fun loadActionsFromPlugins() = sequenceOf<Action>() // TODO: load actions from plugins

private fun loadBuiltInActions() = sequenceOf(
    CreateDatabaseAction,
    FullScreenAction,
    MaximizeWindowAction,
    NewEntryAction,
    OpenAppInfoAction,
    OpenClipboardViewerAction,
    OpenContactInfoAction,
    OpenDatabaseAction,
    OpenDatabaseManagerAction,
    OpenPluginDirAction,
    OpenPluginManagerAction,
    OpenSettingsAction,
    RestartApplicationAction,
    SearchForUpdatesAction,
    QuitAppAction,
    CloseWindowAction
)