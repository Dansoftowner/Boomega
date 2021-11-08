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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.i18n.i18n
import javafx.scene.control.Menu

class PluginMenu(
    private val context: Context,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) : Menu(i18n("menubar.menu.plugin")) {

    init {
        this.menuItem(pluginManagerMenuItem())
            .menuItem(pluginDirectoryItem())
    }

    private fun pluginManagerMenuItem() =
        menuItemOf(GlobalActions.OPEN_PLUGIN_MANAGER, context, preferences, databaseTracker).apply {
            isDisable = true // TODO: unlock plugin manager
        }

    private fun pluginDirectoryItem() =
        menuItemOf(GlobalActions.OPEN_PLUGIN_DIR, context, preferences, databaseTracker)
}