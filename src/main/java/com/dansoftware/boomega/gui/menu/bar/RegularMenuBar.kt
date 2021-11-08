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
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.menu.*
import com.dansoftware.boomega.gui.menu.file.RegularFileMenu
import com.dansoftware.boomega.gui.menu.help.RegularHelpMenu

/**
 * The menu-bar used on every operating system **except on macOS**.
 *
 * @see MacOsMenuBar
 */
class RegularMenuBar(databaseView: DatabaseView, preferences: Preferences, tracker: DatabaseTracker) :
    BaseMenuBar(databaseView) {

    init {
        this.menus.addAll(
            RegularFileMenu(databaseView, databaseView.databaseMeta, preferences, tracker),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView, preferences, tracker),
            ClipboardMenu(databaseView, preferences, tracker),
            WindowMenu(databaseView, preferences, tracker),
            PluginMenu(databaseView, preferences, tracker),
            RegularHelpMenu(databaseView, preferences, tracker)
        )
    }
}
