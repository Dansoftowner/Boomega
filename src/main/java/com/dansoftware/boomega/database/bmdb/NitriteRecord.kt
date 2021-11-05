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

package com.dansoftware.boomega.database.bmdb

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.database.api.data.Record.Type
import com.dansoftware.boomega.database.api.data.ServiceConnection
import com.fasterxml.jackson.annotation.JsonIgnore
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import java.time.LocalDate
import java.util.*

class NitriteRecord @JvmOverloads constructor(@field:JsonIgnore private val baseRecord: Record? = null) {

    @field:Id
    var id: NitriteId? = baseRecord?.id?.let(NitriteId::createId)
        set(value) {
            field = value
            baseRecord?.id = value?.idValue
        }

    var type: Type = baseRecord?.type ?: Type.BOOK
        set(value) {
            field = value
            baseRecord?.type = value
        }

    var title: String? = baseRecord?.title
        set(value) {
            field = value
            baseRecord?.title = value
        }

    var language: Locale? = baseRecord?.language
        set(value) {
            field = value
            baseRecord?.language = language
        }

    var publisher: String? = baseRecord?.publisher
        set(value) {
            field = value
            baseRecord?.publisher = value
        }

    // for allowing the nitrite database to serialize/deserialize the LocalDate object
    @field:com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = LocalDateDeserializer::class)
    @field:com.fasterxml.jackson.databind.annotation.JsonSerialize(using = LocalDateSerializer::class)
    var publishedDate: LocalDate? = baseRecord?.publishedDate
        set(value) {
            field = value
            baseRecord?.publishedDate = value
        }

    var notes: String? = baseRecord?.notes
        set(value) {
            field = value
            baseRecord?.notes = value
        }

    var rating: Int? = baseRecord?.rating
        set(value) {
            field = value
            baseRecord?.rating = value
        }

    var subtitle: String? = baseRecord?.subtitle
        set(value) {
            field = value
            baseRecord?.subtitle = value
        }

    var isbn: String? = baseRecord?.isbn
        set(value) {
            field = value
            baseRecord?.isbn = value
        }

    var numberOfCopies: Int? = baseRecord?.numberOfCopies
        set(value) {
            field = value
            baseRecord?.numberOfCopies = value
        }

    var authors: List<String>? = baseRecord?.authors
        set(value) {
            field = value
            baseRecord?.authors = value
        }

    var subject: String? = baseRecord?.subject
        set(value) {
            field = value
            baseRecord?.subject = value
        }

    var magazineName: String? = baseRecord?.magazineName
        set(value) {
            field = value
            baseRecord?.magazineName = value
        }

    var serviceConnection: ServiceConnection? = baseRecord?.serviceConnection
        set(value) {
            field = value
            baseRecord?.serviceConnection = value?.copy()
        }

    fun toBaseRecord(): Record {
        return Record(
            id = id?.idValue,
            type = type,
            title = title,
            language = language,
            publisher = publisher,
            publishedDate = publishedDate,
            notes = notes,
            rating = rating,
            subtitle = subtitle,
            isbn = isbn,
            numberOfCopies = numberOfCopies,
            authors = authors,
            subject = subject,
            magazineName = magazineName,
            serviceConnection = serviceConnection
        )
    }

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is NitriteRecord -> false
            else -> other.id == this.id
        }
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}