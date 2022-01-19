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
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.action.impl.*
import com.dansoftware.boomega.gui.api.Context
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Deprecated("")
object GlobalActions {

    private val logger: Logger = LoggerFactory.getLogger(GlobalActions::class.java)

    @Deprecated(
        "Use the NewEntryAction object instead",
        replaceWith = ReplaceWith("NewEntryAction", "com.dansoftware.boomega.gui.action.impl.NewEntryAction")
    )
    @JvmField
    val NEW_ENTRY = NewEntryAction

    @Deprecated(
        "Use the OpenDatabaseAction object instead",
        replaceWith = ReplaceWith("OpenDatabaseAction", "com.dansoftware.boomega.gui.action.impl.OpenDatabaseAction")
    )
    @JvmField
    val OPEN_DATABASE = OpenDatabaseAction

    @Deprecated(
        "Use the OpenDatabaseAction object instead",
        replaceWith = ReplaceWith(
            "CreateDatabaseAction",
            "com.dansoftware.boomega.gui.action.impl.CreateDatabaseAction"
        )
    )
    @JvmField
    val CREATE_DATABASE = CreateDatabaseAction

    @Deprecated(
        "Use the OpenDatabaseManagerAction object instead",
        replaceWith = ReplaceWith(
            "OpenDatabaseManagerAction",
            "com.dansoftware.boomega.gui.action.impl.OpenDatabaseManagerAction"
        )
    )
    @JvmField
    val OPEN_DATABASE_MANAGER = OpenDatabaseManagerAction

    @Deprecated(
        "Use the RestartApplicationAction object instead",
        replaceWith = ReplaceWith(
            "RestartApplicationAction",
            "com.dansoftware.boomega.gui.action.impl.RestartApplicationAction"
        )
    )
    @JvmField
    val RESTART_APPLICATION = RestartApplicationAction

    @Deprecated(
        "Use the OpenSettingsAction object instead",
        replaceWith = ReplaceWith("OpenSettingsAction", "com.dansoftware.boomega.gui.action.impl.OpenSettingsAction")
    )
    @JvmField
    val OPEN_SETTINGS = OpenSettingsAction

    @Deprecated(
        "Use the FullScreenAction object instead",
        replaceWith = ReplaceWith("FullScreenAction", "com.dansoftware.boomega.gui.action.impl.FullScreenAction")
    )
    @JvmField
    val FULL_SCREEN = FullScreenAction

    @Deprecated(
        "Use the MaximizeWindowAction object instead",
        replaceWith = ReplaceWith(
            "MaximizeWindowAction",
            "com.dansoftware.boomega.gui.action.impl.MaximizeWindowAction"
        )
    )
    @JvmField
    val MAXIMIZE_WINDOW = MaximizeWindowAction

    @Deprecated(
        "Use the OpenClipboardViewer object instead",
        replaceWith = ReplaceWith("OpenClipboardViewerAction", "com.dansoftware.boomega.gui.action.impl.OpenClipboardViewerAction")
    )
    @JvmField
    val OPEN_CLIPBOARD_VIEWER = OpenClipboardViewerAction

    @Deprecated(
        "Use the OpenPluginManagerAction object instead",
        replaceWith = ReplaceWith(
            "OpenPluginManagerAction",
            "com.dansoftware.boomega.gui.action.impl.OpenPluginManagerAction"
        )
    )
    @JvmField
    val OPEN_PLUGIN_MANAGER = OpenPluginManagerAction

    @Deprecated(
        "Use the OpenPluginDirAction object instead",
        replaceWith = ReplaceWith("OpenPluginDirAction", "com.dansoftware.boomega.gui.action.impl.OpenPluginDirAction")
    )
    @JvmField
    val OPEN_PLUGIN_DIR = OpenPluginDirAction

    @Deprecated(
        "Use the SearchForUpdatesAction object instead",
        replaceWith = ReplaceWith(
            "SearchForUpdatesAction",
            "com.dansoftware.boomega.gui.action.impl.SearchForUpdatesAction"
        )
    )
    @JvmField
    val SEARCH_FOR_UPDATES = SearchForUpdatesAction

    @Deprecated(
        "Use the OpenContactInfoAction object instead",
        replaceWith = ReplaceWith(
            "OpenContactInfoAction",
            "com.dansoftware.boomega.gui.action.impl.OpenContactInfoAction"
        )
    )
    @JvmField
    val OPEN_CONTACT_INFO = OpenContactInfoAction

    @Deprecated(
        "Use the OpenAppInfoAction object instead",
        replaceWith = ReplaceWith("OpenAppInfoAction", "com.dansoftware.boomega.gui.action.impl.OpenAppInfoAction")
    )
    @JvmField
    val OPEN_APP_INFO = OpenAppInfoAction

    /* <-------------------------------------------------------------------- */

    @Deprecated(
        "Use the AvailableActions instead",
        replaceWith = ReplaceWith("AvailableActions", "com.dansoftware.boomega.gui.action.AvailableActions")
    )
    val allActions: List<Action> by lazy {
        javaClass.declaredFields
            .filter { Action::class.java.isAssignableFrom(it.type) }
            .mapNotNull { it.get(null) as Action? }
    }

    /**
     * Applies the key-binding actions on the given scene.
     */
    @Deprecated(
        "Use AvailableActions.applyOnScene instead",
        replaceWith = ReplaceWith(
            "AvailableActions.applyOnScene",
            "com.dansoftware.boomega.gui.action.AvailableActions"
        )
    )
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
}