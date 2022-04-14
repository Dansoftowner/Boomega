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

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.util.os.OsInfo
import javafx.beans.binding.Bindings
import javafx.scene.control.MenuBar

/**
 * Provides the preferred menu-bar for a [DatabaseView] depending on what platform the app is running on.
 */
fun getPreferredApplicationMenuBar(databaseView: DatabaseView): MenuBar =
    when {
        OsInfo.isMacOS -> MacOsFullMenuBar(databaseView)
        else -> FullMenuBar(databaseView)
    }

/**
 * Provides the preferred minimal/simplified menu-bar depending on what platform the app is running on.
 * It might be _null_ if general menu-bars shouldn't be used on the platform.
 */
fun getPreferredGeneralMenuBar(context: Context): MenuBar =
    when {
        OsInfo.isMacOS -> MacOsGeneralMenuBar(context)
        else -> GeneralMenuBar(context)
    }

/**
 * The general menu-bar that requires no opened database.
 * Can be used on most operating systems (expect on macOS).
 *
 * @see MacOsGeneralMenuBar
 */
class GeneralMenuBar(context: Context) : MenuBar() {
    init {
        initDisablePolicy(context)
        menus.addAll(
            CommonFileMenu(context),
            PreferencesMenu(context),
            ClipboardMenu(context),
            WindowMenu(context),
            PluginMenu(context),
            CommonHelpMenu(context)
        )
    }
}

/**
 * The general menu-bar that requires no opened database.
 * Should be used on macOS.
 *
 * @see GeneralMenuBar
 */
class MacOsGeneralMenuBar(context: Context) : MenuBar() {
    init {
        initDisablePolicy(context)
        menus.addAll(
            MacOsApplicationMenu(context),
            MacOsCommonFileMenu(context),
            PreferencesMenu(context),
            ClipboardMenu(context),
            WindowMenu(context),
            PluginMenu(context),
            MacOsHelpMenu(context)
        )
    }
}


/**
 * The menu-bar can be used on most desktop environments **except on macOS**.
 * It can be only used with a [DatabaseView].
 *
 * @see MacOsFullMenuBar
 */
class FullMenuBar(databaseView: DatabaseView) : MenuBar() {

    init {
        initDisablePolicy(databaseView)
        menus.addAll(
            FullFileMenu(databaseView, databaseView.databaseMeta),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView),
            ClipboardMenu(databaseView),
            WindowMenu(databaseView),
            PluginMenu(databaseView),
            CommonHelpMenu(databaseView)
        )
    }
}

/**
 * The menu-bar can be used on most desktop environments **except on macOS**.
 * It can be only used with a [DatabaseView].
 *
 * @see FullMenuBar
 */
class MacOsFullMenuBar(databaseView: DatabaseView) : MenuBar() {
    init {
        initDisablePolicy(databaseView)
        menus.addAll(
            MacOsApplicationMenu(databaseView),
            MacOsFullFileMenu(databaseView, databaseView.databaseMeta),
            ModuleMenu(databaseView),
            PreferencesMenu(databaseView),
            ClipboardMenu(databaseView),
            WindowMenu(databaseView),
            PluginMenu(databaseView),
            MacOsHelpMenu(databaseView)
        )
    }
}

private fun MenuBar.initDisablePolicy(context: Context) {
    properties["overlays.visible.listener"] = // for keeping it in the memory
        Bindings.isEmpty(context.blockingOverlaysShown).and(Bindings.isEmpty(context.nonBlockingOverlaysShown))
            .also { observable ->
                observable.addListener { _, _, isEmpty ->
                    this.isDisable = isEmpty.not()
                }
            }
}