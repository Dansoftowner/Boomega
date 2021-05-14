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

package com.dansoftware.boomega.gui.recordview

import com.dansoftware.boomega.db.Database
import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.gui.context.Context
import javafx.concurrent.Task

/**
 * Task for loading books from the database
 */
open class RecordsGetTask(private val database: Database) :
    Task<List<Record>>() {

    override fun call(): List<Record> {
        return database.records
    }
}

/**
 * A [RecordsGetTask] implementation for loading books from the database into
 * a [RecordTable].
 */
class TableRecordsGetTask(
    context: Context,
    tableView: RecordTable,
    database: Database,
) : RecordsGetTask(database) {
    init {
        setOnRunning { context.showIndeterminateProgress() }
        setOnFailed {
            //TODO: DIALOG
            context.stopProgress()
        }
        setOnSucceeded {
            context.stopProgress()
            tableView.items.setAll(this.value)
            tableView.refresh()
        }
    }
}