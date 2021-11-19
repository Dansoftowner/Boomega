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

package com.dansoftware.boomega.database.sql

import com.dansoftware.boomega.database.api.*
import com.dansoftware.boomega.database.api.data.Record
import org.hibernate.Session
import org.hibernate.boot.Metadata
import org.hibernate.boot.MetadataSources
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.service.ServiceRegistry
import javax.persistence.criteria.CriteriaQuery

class SQLDatabase(
    override val meta: DatabaseMeta,
    hibernateOptions: Map<String, String?>
) : Database {

    private val listeners: MutableSet<DatabaseChangeListener> = HashSet()

    private val serviceRegistry: ServiceRegistry = StandardServiceRegistryBuilder()
        .applySettings(hibernateOptions)
        .build()

    private val metadata: Metadata = MetadataSources(serviceRegistry)
        .addAnnotatedClass(SQLRecord::class.java)
        .buildMetadata()

    private val sessionFactory = metadata.buildSessionFactory()

    override val records: List<Record>
        get() {
            return transaction { session ->
                val cq: CriteriaQuery<SQLRecord> = session.criteriaBuilder
                    .createQuery(SQLRecord::class.java)
                val query: CriteriaQuery<SQLRecord> = cq.select(cq.from(SQLRecord::class.java))
                session.createQuery(query).resultList.map { it.toBaseRecord() }
            }
        }

    override val totalRecordCount: Int
        get() = transaction { session ->
            session.createQuery("SELECT COUNT(*) FROM BoomegaRecords;").singleResult as Int
        }

    override val isClosed: Boolean
        get() = sessionFactory.isClosed

    override fun insertRecord(record: Record) {
        val sqlRecord = SQLRecord(record)
        transaction { it.save(sqlRecord) }
        record.id = sqlRecord.id
        notifyListeners(DatabaseChangeType.INSERT, listOf(record))
    }

    override fun updateRecord(record: Record) {
        transaction {
            it.update(SQLRecord(record))
        }
        notifyListeners(DatabaseChangeType.UPDATE, listOf(record))
    }

    override fun removeRecord(record: Record) {
        transaction {
            it.remove(record)
        }
        notifyListeners(DatabaseChangeType.DELETE, listOf(record))
    }

    override fun removeRecords(records: List<Record>) {
        transaction {
            records.forEach(::removeRecord)
        }
        notifyListeners(DatabaseChangeType.DELETE, records.toList())
    }

    override fun close() {
        sessionFactory.close()
        serviceRegistry.close()
    }

    override fun addListener(listener: DatabaseChangeListener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: DatabaseChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(eventType: DatabaseChangeType, items: List<Record>) {
        listeners.forEach { it.onChange(DatabaseChangeEvent(eventType, items)) }
    }

    private inline fun <T> transaction(action: (Session) -> T): T {
        sessionFactory.openSession().use {
            with(it.beginTransaction()) {
                val actionValue = action(it)
                commit()
                return actionValue
            }
        }
    }
}