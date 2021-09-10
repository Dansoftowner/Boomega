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
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.action.MenuItems
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import com.dansoftware.boomega.i18n.i18n
import javafx.application.Platform
import javafx.scene.control.MenuItem

/**
 * The file menu used on Windows/Linux.
 */
class RegularFileMenu(
    private val context: Context,
    databaseMeta: DatabaseMeta,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) : FileMenu(context, databaseMeta, preferences, databaseTracker) {
    init {
        this.separator()
            .menuItem(closeWindowMenuItem())
            .menuItem(restartMenuItem())
            .menuItem(quitMenuItem())
    }

    private fun closeWindowMenuItem() = MenuItem(i18n("menubar.menu.file.closewindow"))
        .action { context.close() }
        .graphic("close-icon")

    private fun restartMenuItem() =
        MenuItems.of(GlobalActions.RESTART_APPLICATION, context, preferences, databaseTracker)

    private fun quitMenuItem() = MenuItem(i18n("menubar.menu.file.quit"))
        .action { Platform.exit() }
        .graphic("close-box-multiple-icon")
}