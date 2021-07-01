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

package com.dansoftware.boomega.gui.databaseview

import com.dansoftware.boomega.gui.menubar.AppMenuBar
import com.dansoftware.boomega.gui.window.BaseWindow

private class DatabaseWindow(view: DatabaseView, menuBar: AppMenuBar) :
    BaseWindow<DatabaseView>("${System.getProperty("app.name")} - ${view.openedDatabase}", menuBar, view) {
    init {
        this.isMaximized = true
        this.exitDialog = true
        this.minWidth = 530.0
        this.minHeight = 530.0
    }
}