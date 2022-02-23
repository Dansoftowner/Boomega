/*
 * Boomega
 * Copyright (C)  2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.tracking.DatabaseTracker
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.action.impl.OpenSettingsAction
import com.dansoftware.boomega.gui.action.menuItemOf
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.util.action
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.I18N
import javafx.geometry.Insets
import javafx.scene.control.*

/**
 * The toolbar that appears on the top of the login-view.
 */
class LoginToolbar(
    private val context: Context,
    private val databaseTracker: DatabaseTracker,
    private val preferences: Preferences
) : BiToolBar() {

    init {
        leftToolBar.padding = Insets(0.0, 10.0, 0.0, 10.0)
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildLogo())
        leftItems.add(buildLabel())
        rightItems.add(buildQuickOptionsControl())
        rightItems.add(buildInfoItem())
    }

    private fun buildLogo() = icon("login-icon")

    private fun buildLabel() = Label(I18N.getValue("database.auth"))

    private fun buildInfoItem() = Button().apply {
        graphic = icon("info-icon")
        setOnAction { GlobalActions.OPEN_APP_INFO.invoke(context, preferences, databaseTracker) }
    }

    private fun buildQuickOptionsControl() = MenuButton().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("settings-icon")
        items.addAll(
            buildUpdateSearchMenuItem(),
            buildPluginManagerMenuItem(),
            buildSettingsMenuItem()
        )
    }

    private fun buildUpdateSearchMenuItem() =
        menuItemOf(GlobalActions.SEARCH_FOR_UPDATES, context, preferences, databaseTracker)

    private fun buildPluginManagerMenuItem() =
        MenuItem(I18N.getValue("action.open_plugin_manager"), icon("puzzle-icon")).action { }.apply { isDisable = true } // TODO: unlock plugin manager

    private fun buildSettingsMenuItem() =
        MenuItem(I18N.getValue("action.settings"), icon("settings-icon")).action {
            OpenSettingsAction(context, preferences, databaseTracker)
        }
}