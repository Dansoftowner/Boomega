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

import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker
import javafx.scene.layout.VBox

/**
 * The core of a [DatabaseView]. It includes the [DatabaseViewToolbar] and the [TabView].
 */
class DatabaseViewBase(
    view: DatabaseView,
    preferences: com.dansoftware.boomega.config.Preferences,
    databaseTracker: DatabaseTracker
) : VBox() {

    private val toolbar = DatabaseViewToolbar(view, preferences, databaseTracker)
    private val tabView = TabView(ModuleView.getTabItem(view))

    init {
        buildUI()
    }

    private fun buildUI() {
        children.add(toolbar)
        children.add(tabView)
    }

    fun openTab(tabItem: TabItem) = tabView.openTab(tabItem)

    fun closeTab(tabItem: TabItem) = tabView.closeTab(tabItem)
}