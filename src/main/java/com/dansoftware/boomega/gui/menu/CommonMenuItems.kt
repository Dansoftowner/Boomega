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

/**
 * Contains typical menu-items used by different Menus.
 */
@file:JvmName("CommonMenuItems")

package com.dansoftware.boomega.gui.menu

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.graphic
import com.dansoftware.boomega.i18n.i18n
import javafx.application.Platform
import javafx.scene.control.MenuItem

// TODO create actions instead of these

/**
 * Menu-item that closes the current window
 */
fun closeWindowMenuItem(context: Context) = MenuItem(i18n("menubar.menu.file.closewindow"))
    .action { context.close() }
    .graphic("close-icon")

/**
 * Menu-item that shuts down the whole applicaton
 */
fun quitMenuItem() = MenuItem(i18n("menubar.menu.file.quit"))
    .action { Platform.exit() }
    .graphic("close-box-multiple-icon")