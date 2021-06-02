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
package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.action.Action
import com.dansoftware.boomega.gui.action.KeyBindingAction
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.keybinding.keyBinding
import javafx.scene.control.MenuItem

object MenuItems {
    fun of(
        action: Action,
        context: Context,
        preferences: Preferences,
        databaseTracker: DatabaseTracker
    ): MenuItem {
        return MenuItem(action.displayName)
            .action { action.invoke(context, preferences, databaseTracker) }
            .graphic(action.icon)
    }

    fun of(
        action: KeyBindingAction,
        context: Context,
        preferences: Preferences,
        databaseTracker: DatabaseTracker
    ): MenuItem {
        return MenuItem(action.displayName)
            .action { action.invoke(context, preferences, databaseTracker) }
            .keyBinding(action.keyBinding)
            .graphic(action.icon)
    }
}