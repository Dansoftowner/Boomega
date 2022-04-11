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
package com.dansoftware.boomega.database.bmdb

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.database.api.data.Record
import org.dizitart.no2.Nitrite
import org.dizitart.no2.objects.ObjectRepository
import org.slf4j.LoggerFactory
import java.util.Collections.unmodifiableList

/**
 * A NitriteDatabase is a [Database] implementation that basically wraps the
 * Nitrite database api.
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 */
open class NitriteDatabase(
    private val nitriteClient: Nitrite,
    override val meta: DatabaseMeta
) : Database {

    private val listeners: MutableSet<DatabaseChangeListener> = HashSet()

    private val recordRepository: ObjectRepository<NitriteRecord> =
        nitriteClient.getRepository(REPOSITORY_KEY, NitriteRecord::class.java)

    override val totalRecordCount: Int
        get() = recordRepository.find().totalCount()

    override val records: List<Record>
        get() = recordRepository.find().map { it.toBaseRecord() }

    override val isClosed: Boolean
        get() = nitriteClient.isClosed

    @Synchronized
    override fun insertRecord(record: Record) {
        val nitriteRecord = NitriteRecord(record)
        recordRepository.insert(nitriteRecord)
        record.id = nitriteRecord.id!!.idValue
        notifyListeners(DatabaseChangeType.INSERT, listOf(record))
    }

    @Synchronized
    override fun updateRecord(record: Record) {
        recordRepository.update(NitriteRecord(record))
        notifyListeners(DatabaseChangeType.UPDATE, listOf(record))
    }

    @Synchronized
    override fun removeRecord(record: Record) {
        recordRepository.remove(NitriteRecord(record))
        notifyListeners(DatabaseChangeType.DELETE, listOf(record))
    }

    @Synchronized
    override fun removeRecords(records: List<Record>) {
        records.map(::NitriteRecord).forEach(recordRepository::remove)
        notifyListeners(DatabaseChangeType.DELETE, unmodifiableList(records))
    }

    @Synchronized
    override fun close() {
        nitriteClient.close()
    }

    @Synchronized
    override fun addListener(listener: DatabaseChangeListener) {
        listeners.add(listener)
    }

    @Synchronized
    override fun removeListener(listener: DatabaseChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(eventType: DatabaseChangeType, items: List<Record>) {
        listeners.forEach { it.onChange(DatabaseChangeEvent(eventType, items)) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(NitriteDatabase::class.java)
        private const val REPOSITORY_KEY = "BoomegaRecords"
    }
}