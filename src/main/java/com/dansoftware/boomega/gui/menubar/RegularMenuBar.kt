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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.entry.DatabaseTracker

/**
 * The menu-bar used on Windows/Linux.
 */
class RegularMenuBar(databaseView: DatabaseView, preferences: Preferences, tracker: DatabaseTracker) :
    javafx.scene.control.MenuBar() {

    init {
        this.menus.addAll(
            RegularFileMenu(databaseView, databaseView.databaseMeta, preferences, tracker),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView, preferences, tracker),
            ClipboardMenu(databaseView, preferences, tracker),
            WindowMenu(databaseView, preferences, tracker),
            PluginMenu(databaseView, preferences, tracker),
            HelpMenu(databaseView, preferences, tracker)
        )
    }
}
