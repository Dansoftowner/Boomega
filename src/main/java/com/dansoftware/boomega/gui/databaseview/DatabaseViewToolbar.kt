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

import com.dansoftware.boomega.config.PreferenceKey
import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.gui.action.GlobalActions
import com.dansoftware.boomega.gui.control.TwoSideToolBar
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.util.revealInExplorer
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Orientation
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane

class DatabaseViewToolbar(
    private val view: DatabaseView,
    private val preferences: Preferences,
    private val databaseTracker: DatabaseTracker
) : TwoSideToolBar() {

    init {
        styleClass.add("database-view-toolbar")
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildHomeButton())
        leftItems.add(buildSeparator())
        leftItems.add(buildDatabaseNameControl())
        rightItems.add(buildFileOpenerButton())
        rightItems.add(buildSeparator())
        rightItems.add(buildCloseButton())
    }

    private fun buildHomeButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.HOME)
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
                    MaterialDesignIconView(MaterialDesignIcon.DATABASE),
                    Label(view.openedDatabase.toString())
                )
            )
        )

    private fun buildCloseButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.LOGOUT)
        tooltip = Tooltip(I18N.getValue("menubar.menu.file.dbclose"))
        setOnAction {
            preferences.editor()
                .put(PreferenceKey.LOGIN_DATA, preferences.get(PreferenceKey.LOGIN_DATA).apply {
                    if (autoLoginDatabase.equals(view.openedDatabase)) {
                        isAutoLogin = false
                        autoLoginCredentials = null
                    }
                }).tryCommit()
            view.close()
            GlobalActions.NEW_ENTRY.invoke(view, preferences, databaseTracker)
        }
    }

    private fun buildFileOpenerButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.FOLDER_OPEN)
        tooltip = Tooltip(I18N.getValue("menubar.menu.file.reveal"))
        setOnAction {
            view.openedDatabase.file.revealInExplorer()
        }
    }
}