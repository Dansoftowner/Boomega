package com.dansoftware.libraryapp.gui.record.show

import com.dansoftware.libraryapp.db.Database
import com.dansoftware.libraryapp.db.data.Book
import com.dansoftware.libraryapp.gui.context.Context
import javafx.concurrent.Task

/**
 * Task for loading books from the database
 */
open class BooksGetTask(private val database: Database, private val offSet: Int, private val size: Int) :
    Task<List<Book>>() {

    override fun call(): List<Book> {
        return database.getBooks(offSet, size)
    }
}

/**
 * A [BooksGetTask] implementation for loading books from the database into
 * a [BooksTable].
 */
class TableBooksGetTask(
    context: Context,
    tableView: BooksTable,
    database: Database,
    offSet: Int,
    size: Int
) : BooksGetTask(database, offSet, size) {
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