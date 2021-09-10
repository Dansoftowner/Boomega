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

import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.menuItem
import com.dansoftware.boomega.i18n.i18n
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

class ModuleMenu(val view: DatabaseView) : Menu(i18n("menubar.menu.modules")) {
    init {
        view.modules.forEach {
            this.menuItem(MenuItem(it.name, it.icon).action { _ -> view.openModule(it) })
        }
    }
}