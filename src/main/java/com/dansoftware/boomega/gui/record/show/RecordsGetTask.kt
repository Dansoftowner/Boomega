package com.dansoftware.boomega.gui.record.show

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