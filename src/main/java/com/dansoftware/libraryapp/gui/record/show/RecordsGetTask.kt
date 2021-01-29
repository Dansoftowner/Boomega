package com.dansoftware.libraryapp.gui.record.show

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Record
import com.dansoftware.libraryapp.gui.context.Context
import javafx.concurrent.Task

/**
 * Task for loading books from the database
 */
open class RecordsGetTask(private val database: Database, private val offSet: Int, private val size: Int) :
    Task<List<Record>>() {

    override fun call(): List<Record> {
        return database.getRecords(offSet, size)
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
    offSet: Int,
    size: Int
) : RecordsGetTask(database, offSet, size) {
    init {
        setOnRunning { context.showIndeterminateProgress() }
        setOnFailed {
            //TODO: DIALOG
            context.stopProgress()
        }
        setOnSucceeded {
            context.stopProgress()
            when (offSet) {
                0 -> tableView.items.setAll(this.value)
                else -> tableView.items.addAll(offSet, this.value)
            }
            tableView.refresh()
        }
    }
}