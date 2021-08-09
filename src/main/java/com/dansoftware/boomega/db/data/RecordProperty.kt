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

import com.dansoftware.boomega.i18n.i18n

/**
 * Represents a user-visible field of a [Record].
 *
 * @property id the unique identifier of the property; should be identical with the actual name of the [Record]'s field
 * @property name gives the localized, user-visible name of the property
 * @property getValue gets the value from the given [Record]
 * @property setValue sets the value in the given [Record]
 * @property typeScopes the specific [Record.Type]s the property used with
 */
class RecordProperty<T> private constructor(
    val id: String,
    val name: () -> (String),
    val getValue: Record.() -> T,
    val setValue: Record.(T) -> Unit,
    vararg val typeScopes: Record.Type
) {

    private constructor(id: String, name: () -> String, getValue: Record.() -> T, setValue: Record.(T) -> Unit) : this(
        id,
        name,
        getValue,
        setValue,
        *Record.Type.values()
    )

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {

        @JvmField
        val TYPE = RecordProperty(
            id = "type",
            name = { i18n("record.property.type") },
            getValue = Record::type,
            setValue = { type = it }
        )

        @JvmField
        val TITLE = RecordProperty(
            id = "title",
            name = { i18n("record.property.title") },
            getValue = Record::title,
            setValue = { title = it }
        )

        @JvmField
        val LANGUAGE = RecordProperty(
            id = "language",
            name = { i18n("record.property.lang") },
            getValue = Record::language,
            setValue = { language = it }
        )

        @JvmField
        val PUBLISHER = RecordProperty(
            id = "publisher",
            name = { i18n("record.property.publisher") },
            getValue = Record::publisher,
            setValue = { publisher = it }
        )

        @JvmField
        val PUBLISHED_DATE = RecordProperty(
            id = "publishedDate",
            name = { i18n("record.property.published_date") },
            getValue = Record::publishedDate,
            setValue = { publishedDate = it }
        )

        @JvmField
        val NOTES = RecordProperty(
            id = "notes",
            name = { i18n("record.property.notes") },
            getValue = Record::notes,
            setValue = { notes = it }
        )

        @JvmField
        val RATING = RecordProperty(
            id = "rating",
            name = { i18n("record.property.rating") },
            getValue = Record::rating,
            setValue = { rating = it }
        )

        @JvmField
        val SUBTITLE = RecordProperty(
            id = "subtitle",
            name = { i18n("record.property.subtitle") },
            getValue = Record::subtitle,
            setValue = { subtitle = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val ISBN = RecordProperty(
            id = "isbn",
            name = { i18n("record.property.isbn") },
            getValue = Record::isbn,
            setValue = { isbn = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val NUMBER_OF_COPIES =
            RecordProperty(
                id = "numberOfCopies",
                name = { i18n("record.property.nofcopies") },
                getValue = Record::numberOfCopies,
                setValue = { numberOfCopies = it },
                typeScopes = arrayOf(Record.Type.BOOK)
            )

        @JvmField
        val AUTHORS = RecordProperty(
            id = "authors",
            name = { i18n("record.property.authors") },
            getValue = Record::authors,
            setValue = { authors = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val SUBJECT = RecordProperty(
            id = "subject",
            name = { i18n("record.property.subject") },
            getValue = Record::subject,
            setValue = { subject = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val MAGAZINE_NAME =
            RecordProperty(
                id = "magazineName",
                name = { i18n("record.property.magazinename") },
                getValue = Record::magazineName,
                setValue = { magazineName = it },
                typeScopes = arrayOf(Record.Type.MAGAZINE)
            )

        @JvmField
        val SERVICE_CONNECTION =
            RecordProperty(
                id = "serviceConnection",
                name = { i18n("record.property.service_connection") },
                getValue = Record::serviceConnection,
                setValue = { throw UnsupportedOperationException() }
            )

        val allProperties = listOf(
            TYPE,
            TITLE,
            LANGUAGE,
            PUBLISHER,
            PUBLISHED_DATE,
            NOTES,
            RATING,
            SUBTITLE,
            ISBN,
            NUMBER_OF_COPIES,
            AUTHORS,
            SUBJECT,
            MAGAZINE_NAME
        )
    }
}