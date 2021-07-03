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

import com.dansoftware.boomega.config.Preferences
import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.base.BaseView
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.googlebooks.GoogleBooksImportModule
import com.dansoftware.boomega.gui.recordview.RecordsViewModule

class DatabaseView(
    private val preferences: Preferences,
    private val database: Database,
    databaseTracker: DatabaseTracker
) : BaseView() {

    val modules: List<Module> = listModules()

    val openedDatabase: DatabaseMeta
        get() = database.meta

    init {
        styleClass.add("database-view")
        content = DatabaseViewBase(this, preferences, databaseTracker)
    }

    fun openTab(tabItem: TabItem) {
        (content as DatabaseViewBase).openTab(tabItem)
    }

    fun openModuleTab() {
        (content as DatabaseViewBase).openModuleTab()
    }

    fun openModule(module: Module) {
        openTab(module.asTabItem())
    }

    private fun listModules(): List<Module> {
        //TODO: plugin modules
        return listOf(
            RecordsViewModule(this, preferences, database),
            GoogleBooksImportModule(this, preferences)
        )
    }
}