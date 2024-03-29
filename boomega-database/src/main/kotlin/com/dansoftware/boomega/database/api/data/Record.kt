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

package com.dansoftware.boomega.database.api.data

import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.util.format
import java.time.LocalDate
import java.util.*

class Record(
    var id: Long? = null,
    var type: Type = Type.BOOK,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var title: String? = null,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var language: Locale? = null,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var publisher: String? = null,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var publishedDate: LocalDate? = null,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var notes: String? = null,

    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var rating: Int? = null,

    @field:RecordFieldTarget(Type.BOOK)
    var subtitle: String? = null,

    @field:RecordFieldTarget(Type.BOOK)
    var isbn: String? = null,

    @field:RecordFieldTarget(Type.BOOK)
    var numberOfCopies: Int? = null,

    @field:RecordFieldTarget(Type.BOOK)
    var authors: List<String>?,

    @field:RecordFieldTarget(Type.BOOK)
    var subject: String? = null,

    @field:RecordFieldTarget(Type.MAGAZINE)
    var magazineName: String? = null,

    serviceConnection: ServiceConnection? = null
) {
    @field:RecordFieldTarget(Type.BOOK, Type.MAGAZINE)
    var serviceConnection: ServiceConnection? = serviceConnection
        get() = field ?: ServiceConnection().also { field = it }

    constructor() : this(Type.BOOK)

    constructor(type: Type) : this(
        null,
        type,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    fun copy() = Record(
        this.id,
        this.type,
        this.title,
        this.language,
        this.publisher,
        this.publishedDate,
        this.notes,
        this.rating,
        this.subtitle,
        this.isbn,
        this.numberOfCopies,
        this.authors,
        this.subject,
        this.magazineName
    ).also { it.serviceConnection = this.serviceConnection?.copy() }

    fun values(): List<String> = listOfNotNull(
        title,
        isbn,
        language?.displayLanguage,
        magazineName,
        notes,
        publishedDate?.format("yyyy-MM-dd"),
        publisher,
        subject,
        subtitle,
        *authors?.toTypedArray() ?: emptyArray()
    )

    /**
     * Checks equality by the [id]s.
     */
    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is Record -> false
            else -> other.id == this.id
        }
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    enum class Type(private val i18N: String) {
        BOOK("record.type.book"),
        MAGAZINE("record.type.magazine");

        override fun toString(): String {
            return i18n(i18N)
        }
    }

}