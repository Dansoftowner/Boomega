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

package com.dansoftware.boomega.database.api

import com.dansoftware.boomega.database.api.data.Record

/**
 * A Database object can communicate with a particular data source.
 *
 *
 *
 * It supports the CRUD operations with [Record]s.
 *
 * @author Daniel Gyorffy
 */
interface Database {

    /**
     * Returns a [DatabaseMeta] object that holds some meta-information
     * of the database.
     */
    val meta: DatabaseMeta

    /**
     * Gives the list of [Record]s stored in the database.
     */
    val records: List<Record>

    /**
     * Gives the total count of records.
     */
    val totalRecordCount: Int

    /**
     * Checks whether the db is closed.
     *
     * @return `true` if closed; otherwise `false`.
     */
    val isClosed: Boolean

    /**
     * Inserts a record into the database
     */
    fun insertRecord(record: Record)

    /**
     * Updates the record in the database
     */
    fun updateRecord(record: Record)

    /**
     * Deletes the record from the database
     */
    fun removeRecord(record: Record)

    /**
     * Deletes the given records from the database
     */
    fun removeRecords(records: List<Record>)

    /**
     * Closes the database
     */
    fun close()

    /**
     * Adds a database change listener which will be notified whenever the
     * database changes.
     *
     * If the same listener is added more than once, then it will be ignored.
     */
    fun addListener(listener: DatabaseChangeListener)

    /**
     * Removes the given [DatabaseChangeListener] from the list of listeners that are notified whenever the
     * database changes.
     *
     * If the given listener has not been previously registered then this method call is a no-op.
     */
    fun removeListener(listener: DatabaseChangeListener)
}