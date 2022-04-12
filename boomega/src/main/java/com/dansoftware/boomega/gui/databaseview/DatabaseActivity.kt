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

import com.dansoftware.boomega.database.api.Database
import com.dansoftware.boomega.database.api.DatabaseMeta
import com.dansoftware.boomega.di.DIService.get
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.dbmanager.DatabaseTracker
import com.dansoftware.boomega.gui.menu.getPreferredApplicationMenuBar
import javafx.stage.WindowEvent
import java.lang.ref.WeakReference
import java.util.*

class DatabaseActivity(
    private val database: Database
) {

    private val databaseView by lazy { buildDatabaseView() }
    val context: Context get() = databaseView

    init {
        instances.add(WeakReference(this))
        get(DatabaseTracker::class).registerUsedDatabase(database.meta)
    }

    fun show(): Boolean {
        val databaseWindow = buildDatabaseWindow()
        databaseWindow.show()
        return true
    }

    private fun buildDatabaseWindow(): DatabaseWindow {
        val menuBar = getPreferredApplicationMenuBar(databaseView)
        val window = DatabaseWindow(databaseView, menuBar)
        window.addEventHandler(WindowEvent.WINDOW_HIDDEN) {
            database.close()
            get(DatabaseTracker::class).registerClosedDatabase(database.meta)
        }
        return window
    }

    private fun buildDatabaseView() = DatabaseView(get(com.dansoftware.boomega.config.Preferences::class), database, get(
        DatabaseTracker::class))

    companion object {

        private val instances = Collections.synchronizedSet(HashSet<WeakReference<DatabaseActivity>>())

        @JvmStatic
        fun getByDatabase(databaseMeta: DatabaseMeta?): Optional<DatabaseActivity> {
            return instances.asSequence()
                .map { obj: WeakReference<DatabaseActivity> -> obj.get() }
                .find { it?.database?.meta == databaseMeta }
                .let { Optional.ofNullable(it) }
        }
    }
}