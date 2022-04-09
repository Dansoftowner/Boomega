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

import com.dansoftware.boomega.gui.action.impl.OpenAppInfoAction
import com.dansoftware.boomega.gui.action.impl.OpenContactInfoAction
import com.dansoftware.boomega.gui.action.impl.SearchForUpdatesAction
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.api.i18n
import javafx.scene.control.Menu

/**
 * The base help menu. Supertype of all kind of os specific help-menus.
 */
abstract class HelpMenu(private val context: Context) : Menu(i18n("menubar.menu.help")) {

    init {
        items.add(contactMenuItem())
    }

    private fun contactMenuItem() =
        menuItemOf(OpenContactInfoAction, context)
}

/**
 * The help menu used on all operating systems **except on macOS**.
 */
class CommonHelpMenu(context: Context) : HelpMenu(context) {
    init {
        items.add(0, menuItemOf(SearchForUpdatesAction, context))
        items.add(menuItemOf(OpenAppInfoAction, context))
    }
}

/**
 * The help-menu used on macOS
 */
class MacOsHelpMenu(context: Context) : HelpMenu(context)