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

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.gui.action.*
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.gui.util.separator
import javafx.scene.control.Menu

/**
 * The application menu that's used only in the macOS menu-bar.
 *
 * @see com.dansoftware.boomega.gui.menu.bar.MacOsMenuBar
 */
class MacOsApplicationMenu(context: Context) : Menu(System.getProperty("app.name")) {

    init {
        this.menuItem(menuItemOf(OpenAppInfoAction, context))
            .menuItem(menuItemOf(SearchForUpdatesAction, context))
            .separator()
            .menuItem(menuItemOf(CloseWindowAction, context))
            .menuItem(menuItemOf(RestartApplicationAction, context))
            .menuItem(menuItemOf(QuitAppAction, context))
    }
}