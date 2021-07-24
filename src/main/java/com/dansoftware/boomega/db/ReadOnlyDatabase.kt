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

class ReadOnlyDatabase(database: Database) : Database by database {
    override fun insertRecord(record: Record) {
        throw UnsupportedOperationException("Action INSERT is not supported")
    }

    override fun updateRecord(record: Record) {
        throw UnsupportedOperationException("Action UPDATE is not supported")
    }

    override fun removeRecord(record: Record) {
        throw UnsupportedOperationException("Action DELETE is not supported")
    }

    override fun removeRecords(records: List<Record>) {
        throw UnsupportedOperationException("Action DELETE is not supported")
    }

    override fun close() {
        throw UnsupportedOperationException("Action CLOSE is not supported")
    }
}