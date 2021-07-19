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
import com.dansoftware.boomega.db.listener.DatabaseChangeEvent
import com.dansoftware.boomega.db.listener.DatabaseChangeListener
import com.dansoftware.boomega.db.listener.DatabaseChangeType
import com.dansoftware.boomega.i18n.i18n
import org.dizitart.no2.Nitrite
import org.dizitart.no2.NitriteBuilder
import org.dizitart.no2.exceptions.ErrorMessage
import org.dizitart.no2.exceptions.NitriteException
import org.dizitart.no2.exceptions.NitriteIOException
import org.dizitart.no2.objects.ObjectRepository
import org.jetbrains.annotations.NonNls
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*
import java.util.Collections.unmodifiableList
import kotlin.collections.HashSet

/**
 * A NitriteDatabase is a [Database] that basically wraps the
 * Nitrite database api.
 *
 * @author Daniel Gyorffy
 * @see Nitrite
 */
open class NitriteDatabase @JvmOverloads constructor(
    private val nitriteClient: Nitrite,
    override val meta: DatabaseMeta = DatabaseMeta(toString(), null)
) : Database {

    private val listeners: MutableSet<DatabaseChangeListener<Record>> = HashSet()

    private val recordRepository: ObjectRepository<Record> =
        nitriteClient.getRepository(REPOSITORY_KEY, Record::class.java)

    override val totalRecordCount: Int
        get() = recordRepository.find().totalCount()

    override val records: List<Record>
        get() = recordRepository.find().toList()

    override val isClosed: Boolean
        get() = nitriteClient.isClosed

    @Synchronized
    override fun insertRecord(record: Record) {
        recordRepository.insert(record)
        notifyListeners(DatabaseChangeType.INSERT, listOf(record))
    }

    @Synchronized
    override fun updateRecord(record: Record) {
        recordRepository.update(record)
        notifyListeners(DatabaseChangeType.UPDATE, listOf(record))
    }

    @Synchronized
    override fun removeRecord(record: Record) {
        recordRepository.remove(record)
        notifyListeners(DatabaseChangeType.DELETE, listOf(record))
    }

    @Synchronized
    override fun removeRecords(records: List<Record>) {
        records.forEach {
            recordRepository.remove(it)
        }
        notifyListeners(DatabaseChangeType.DELETE, unmodifiableList(records))
    }

    @Synchronized
    override fun close() {
        nitriteClient.close()
    }

    @Synchronized
    override fun addListener(listener: DatabaseChangeListener<Record>) {
        listeners.add(listener)
    }

    @Synchronized
    override fun removeListener(listener: DatabaseChangeListener<Record>) {
       listeners.remove(listener)
    }

    private fun notifyListeners(eventType: DatabaseChangeType, items: List<Record>) {
        listeners.forEach { it.onChange(DatabaseChangeEvent(eventType, items)) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(NitriteDatabase::class.java)
        private const val REPOSITORY_KEY = "BoomegaRecords"

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {

        private val nitriteBuilder: NitriteBuilder = Nitrite.builder()

        private var databaseMeta: DatabaseMeta? = null
        private var onFailed: FailListener? = null

        fun onFailed(onFailed: FailListener) = apply {
            this.onFailed = onFailed
        }

        fun compressed() = apply {
            nitriteBuilder.compressed()
        }

        fun autoCommitBufferSize(size: Int) = apply {
            nitriteBuilder.autoCommitBufferSize(size)
        }

        fun filePath(file: File) = apply {
            nitriteBuilder.filePath(file)
        }

        fun databaseMeta(databaseMeta: DatabaseMeta) = apply {
            this.databaseMeta = databaseMeta
            databaseMeta.file?.let { filePath(it) }
        }

        fun build(): NitriteDatabase? =
            createDatabase { nitriteBuilder.openOrCreate() }

        fun build(credentials: Credentials): NitriteDatabase? {
            return if (credentials.isAnonymous) build()
            else createDatabase {
                nitriteBuilder.openOrCreate(credentials.username, credentials.password)
            }
        }

        fun touch() {
            build()?.close()
        }

        fun touch(credentials: Credentials) {
            build(credentials)?.close()
        }

        private fun createDatabase(buildNitrite: () -> Nitrite): NitriteDatabase? {
            return try {
                databaseMeta?.let { NitriteDatabase(buildNitrite(), it) } ?: NitriteDatabase(buildNitrite())
            } catch (e: NitriteException) {
                onFailed?.run { onFail(e.message(), e) }
                null
            }
        }

        @Suppress("NullableBooleanElvis")
        private fun NitriteException.message(): String {
            return i18n(
                when (errorMessage) {
                    ErrorMessage.NO_USER_MAP_FOUND -> "login.failed.null_user_credential"
                    ErrorMessage.USER_MAP_SHOULD_NOT_EXISTS -> "login.failed.authentication_required"
                    ErrorMessage.DATABASE_OPENED_IN_OTHER_PROCESS -> "login.failed.database_opened_in_other_process"
                    ErrorMessage.UNABLE_TO_CREATE_DB_FILE -> "login.failed.unable_to_create_db_file"

                    ErrorMessage.USER_ID_IS_EMPTY,
                    ErrorMessage.PASSWORD_IS_EMPTY,
                    ErrorMessage.NULL_USER_CREDENTIAL,
                    ErrorMessage.INVALID_USER_PASSWORD -> "login.failed.invalid_user_password"

                    else -> if (this is NitriteIOException) "login.failed.io" else "login.failed.security"
                }
            )
        }

        fun interface FailListener {
            fun onFail(message: @NonNls String, throwable: Throwable)
        }
    }

}