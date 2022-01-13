/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.util.os.OsInfo
import javafx.beans.binding.Bindings
import javafx.scene.control.MenuBar

/**
 * Provides the preferred menu-bar for a [DatabaseView] depending on what platform the app is running on.
 */
fun getPreferredApplicationMenuBar(
    databaseView: DatabaseView,
    preferences: Preferences,
    tracker: DatabaseTracker
): MenuBar =
    when {
        OsInfo.isMacOS -> MacOsMenuBar(databaseView, preferences, tracker)
        else -> CommonMenuBar(databaseView, preferences, tracker)
    }

/**
 * Provides the preferred minimal/simplified menu-bar depending on what platform the app is running on.
 * It might be _null_ if general menu-bars shouldn't be used on the platform.
 */
fun getPreferredGeneralMenuBar(
    context: Context,
    preferences: Preferences,
    databaseTracker: DatabaseTracker
): MenuBar? =
    when {
        OsInfo.isMacOS -> MinimalMacOsMenuBar(context, preferences, databaseTracker)
        else -> null
    }

/**
 * The menu-bar can be used on most desktop environments **except on macOS**.
 *
 * @see MacOsMenuBar
 */
class CommonMenuBar(
    databaseView: DatabaseView,
    preferences: Preferences,
    tracker: DatabaseTracker
) : MenuBar() {

    init {
        initDisablePolicy(databaseView)
        this.menus.addAll(
            CommonFileMenu(databaseView, databaseView.databaseMeta, preferences, tracker),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView, preferences, tracker),
            ClipboardMenu(databaseView, preferences, tracker),
            WindowMenu(databaseView, preferences, tracker),
            PluginMenu(databaseView, preferences, tracker),
            CommonHelpMenu(databaseView, preferences, tracker)
        )
    }
}

/**
 * The complete menu-bar can be used on MacOS
 */
class MacOsMenuBar(
    databaseView: DatabaseView,
    preferences: Preferences,
    tracker: DatabaseTracker
) : MenuBar() {
    init {
        initDisablePolicy(databaseView)
        menus.addAll(
            MacOsApplicationMenu(databaseView, preferences, tracker),
            MacOsFileMenu(databaseView, databaseView.databaseMeta, preferences, tracker),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView, preferences, tracker),
            ClipboardMenu(databaseView, preferences, tracker),
            WindowMenu(databaseView, preferences, tracker),
            PluginMenu(databaseView, preferences, tracker),
            MacOsHelpMenu(databaseView, preferences, tracker)
        )
    }
}

/**
 * The simplified MacOS menu-bar with some basic menus:
 * - [MacOsApplicationMenu]
 * - [PreferencesMenu]
 * - [ClipboardMenu]
 * - [PluginMenu]
 * - [WindowMenu]
 */
class MinimalMacOsMenuBar(
    context: Context,
    preferences: Preferences,
    databaseTracker: DatabaseTracker
) : MenuBar() {
    init {
        initDisablePolicy(context)
        menus.addAll(
            MacOsApplicationMenu(context, preferences, databaseTracker),
            PreferencesMenu(context, preferences, databaseTracker),
            ClipboardMenu(context, preferences, databaseTracker),
            PluginMenu(context, preferences, databaseTracker),
            WindowMenu(context, preferences, databaseTracker)
        )
    }
}

private fun MenuBar.initDisablePolicy(context: Context) {
    properties["overlays.visible.listener"] = // for keeping it in the memory
        Bindings.isEmpty(context.blockingOverlaysShown).and(Bindings.isEmpty(context.nonBlockingOverlaysShown))
            .also { observable ->
                observable.addListener { _, _, isEmpty ->
                    this.isDisable = isEmpty.not()
                }
            }
}