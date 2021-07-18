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
package com.dansoftware.boomega.db

import com.dansoftware.boomega.db.data.Record

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

    val records: List<Record>
    val totalRecordCount: Int


    fun insertRecord(record: Record)
    fun updateRecord(record: Record)
    fun removeRecord(record: Record)

    /**
     * Checks whether the db is closed.
     *
     * @return `true` if closed; otherwise `false`.
     */
    val isClosed: Boolean

    /**
     * Closes the database
     */
    fun close()

    /**
     * Returns a [DatabaseMeta] object that holds some meta-information
     * of the database.
     *
     * @return the DatabaseMeta object
     */
    val meta: DatabaseMeta
}