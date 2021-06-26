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

package com.dansoftware.boomega.db.data

import com.dansoftware.boomega.i18n.I18N
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Record(
    @field:Id var id: NitriteId? = null,
    var recordType: Type = Type.BOOK,

    //General
    var title: String? = null,
    var language: String? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var notes: String? = null,
    var rating: Int? = null,

    //Book specific properties
    @field:BookProperty var subtitle: String? = null,
    @field:BookProperty var isbn: String? = null,
    @field:BookProperty var numberOfCopies: Int? = null,
    @field:BookProperty var authors: List<String>?,
    @field:BookProperty var subject: String? = null,

    //Magazine specific properties
    @field:MagazineProperty var magazineName: String? = null
) {
    var serviceConnection: ServiceConnection? = null
        get() = field ?: ServiceConnection().also { field = it }

    constructor() : this(
        null,
        Type.BOOK,
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

    private constructor(builder: Builder) : this(
        null,
        builder.recordType,
        builder.title,
        builder.language,
        builder.publisher,
        builder.publishedDate,
        builder.notes,
        builder.rating,
        builder.subtitle,
        builder.isbn,
        builder.numberOfCopies,
        builder.authors,
        builder.subject,
        builder.magazineName
    ) {
        this.serviceConnection = builder.serviceConnection
    }

    fun copy() = Record(
        this.id,
        this.recordType,
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
        language,
        magazineName,
        notes,
        publishedDate,
        publisher,
        subject,
        subtitle,
        *authors?.toTypedArray() ?: emptyArray()
    )

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is Record -> false
            else -> other.id == this.id
        }
    }

    enum class Type(private val i18N: String) {
        BOOK("record.type.book"), MAGAZINE("record.type.magazine");

        override fun toString(): String {
            return I18N.getValue(i18N)
        }
    }

    class Builder(var recordType: Type) {

        var title: String? = null
            private set
        var language: String? = null
            private set
        var publisher: String? = null
            private set
        var publishedDate: String? = null
            private set
        var notes: String? = null
            private set
        var rating: Int? = null
            private set

        //Book specific properties
        @field:BookProperty
        var subtitle: String? = null
            private set

        @field:BookProperty
        var isbn: String? = null
            private set

        @field:BookProperty
        var numberOfCopies: Int? = null
            private set

        @field:BookProperty
        var authors: List<String>? = null
            private set

        @field:BookProperty
        var subject: String? = null

        //Magazine specific properties
        @field:MagazineProperty
        var magazineName: String? = null
            private set

        var serviceConnection: ServiceConnection? = null
            private set

        fun title(title: String?) = apply { this.title = title }
        fun language(language: String?) = apply { this.language = language }
        fun publisher(publisher: String?) = apply { this.publisher = publisher }
        fun publishedDate(publishedDate: LocalDate?) = apply {
            this.publishedDate = publishedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        fun notes(notes: String?) = apply { this.notes = notes }
        fun rating(rating: Int?) = apply { this.rating = rating }

        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }
        fun isbn(isbn: String?) = apply { this.isbn = isbn }
        fun numberOfCopies(numberOfCopies: Int?) = apply { this.numberOfCopies = numberOfCopies }
        fun authors(authors: List<String>?) = apply { this.authors = authors }
        fun subject(subject: String?) = apply { this.subject = subject }

        fun magazineName(magazineName: String?) = apply { this.magazineName = magazineName }

        fun serviceConnection(serviceConnection: ServiceConnection?) =
            apply { this.serviceConnection = serviceConnection }

        fun build() = Record(this)
    }
}