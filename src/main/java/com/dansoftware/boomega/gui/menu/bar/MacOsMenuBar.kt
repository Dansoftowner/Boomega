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

package com.dansoftware.boomega.gui.menu.bar

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.menu.*
import com.dansoftware.boomega.gui.menu.file.MacOsFileMenu
import com.dansoftware.boomega.gui.menu.help.MacOsHelpMenu
import javafx.scene.control.MenuBar

/**
 * The menu-bar used on macOS
 */
class MacOsMenuBar(
    databaseView: DatabaseView,
    preferences: Preferences,
    tracker: DatabaseTracker
) : MenuBar() {
    init {
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