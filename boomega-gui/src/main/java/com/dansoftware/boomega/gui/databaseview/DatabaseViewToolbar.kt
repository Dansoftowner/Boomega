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

package com.dansoftware.boomega.gui.databaseview

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.action.NewEntryAction
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.login.isAutoLoginOn
import com.dansoftware.boomega.gui.login.removeAutoLogin
import com.dansoftware.boomega.gui.login.updateLoginData
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.byteCountToDisplaySize
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class DatabaseViewToolbar(
    private val view: DatabaseView,
) : BiToolBar() {

    init {
        styleClass.add("database-view-toolbar")
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildHomeButton())
        leftItems.add(buildSeparator())
        leftItems.add(buildDatabaseNameControl())
        rightItems.add(buildSizeIndicator())
        rightItems.add(buildSeparator())
        rightItems.add(buildFileOpenerButton())
        rightItems.add(buildSeparator())
        rightItems.add(buildCloseButton())
    }

    private fun buildHomeButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("home-icon")
        // TODO: tooltip
        setOnAction {
            view.openModuleTab()
        }
    }

    private fun buildSeparator() = Separator(Orientation.VERTICAL)

    private fun buildDatabaseNameControl() =
        StackPane(
            Group(
                HBox(
                    2.0,
                    icon("database-icon"),
                    Label(view.databaseMeta.toString())
                )
            )
        )

    private fun buildCloseButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("logout-icon")
        tooltip = Tooltip(I18N.getValue("menubar.menu.file.dbclose"))
        setOnAction {
            get(Preferences::class).updateLoginData {
                if (it.isAutoLoginOn(view.databaseMeta)) it.removeAutoLogin()
            }
            NewEntryAction.invoke(view)
            view.close()
        }
    }

    private fun buildFileOpenerButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("folder-open-icon")
        tooltip = Tooltip(I18N.getValue("menubar.menu.file.reveal"))
        setOnAction {
            if (view.databaseMeta.isActionSupported(DatabaseMeta.Action.OpenInExternalApplication))
                view.databaseMeta.performAction(DatabaseMeta.Action.OpenInExternalApplication)
        }
    }

    private fun buildSizeIndicator() = Label().apply {
        if (view.databaseMeta.isActionSupported(DatabaseMeta.Action.SizeInBytes)) {
            padding = Insets(0.0, 5.0, 0.0, 0.0)

            val updateText = {
                text = "${i18n("database_view.database_size")} ${
                    byteCountToDisplaySize(
                        view.databaseMeta.performAction(DatabaseMeta.Action.SizeInBytes)
                    )
                }"
            }.also { it() }
            view.databaseReadOnly.addListener {
                Platform.runLater {
                    updateText()
                }
            }
        } else {
            isManaged = false
            isVisible = false
        }
    }
}