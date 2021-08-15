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
import java.util.*

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
    val name: String,
    val typeClassReference: Class<T>,
    val isSortable: Boolean = Comparable::class.java.isAssignableFrom(typeClassReference),
    val getValue: (Record?) -> T?,
    val setValue: Record.(T) -> Unit,
    vararg val typeScopes: Record.Type
) {

    private constructor(
        id: String,
        name: String,
        typeClassReference: Class<T>,
        getValue: (Record?) -> T?,
        setValue: Record.(T) -> Unit
    ) : this(
        id = id,
        name = name,
        typeClassReference = typeClassReference,
        getValue = getValue,
        setValue = setValue,
        typeScopes = Record.Type.values()
    )

    override fun toString(): String {
        return name
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {

        @JvmField
        val TYPE = RecordProperty(
            id = "type",
            name = i18n("record.property.type"),
            typeClassReference = Record.Type::class.java,
            getValue = { it?.type },
            setValue = { type = it }
        )

        @JvmField
        val TITLE = RecordProperty(
            id = "title",
            name = i18n("record.property.title"),
            typeClassReference = String::class.java,
            getValue = { it?.title },
            setValue = { title = it }
        )

        @JvmField
        val LANGUAGE = RecordProperty(
            id = "language",
            name = i18n("record.property.lang"),
            typeClassReference = Locale::class.java,
            isSortable = false,
            getValue = { it?.language },
            setValue = { language = it }
        )

        @JvmField
        val PUBLISHER = RecordProperty(
            id = "publisher",
            name = i18n("record.property.publisher"),
            typeClassReference = String::class.java,
            getValue = { it?.publisher },
            setValue = { publisher = it }
        )

        @JvmField
        val PUBLISHED_DATE = RecordProperty(
            id = "publishedDate",
            name = i18n("record.property.published_date"),
            typeClassReference = String::class.java,
            getValue = { it?.publishedDate },
            setValue = { publishedDate = it }
        )

        @JvmField
        val NOTES = RecordProperty(
            id = "notes",
            name = i18n("record.property.notes"),
            typeClassReference = String::class.java,
            isSortable = false,
            getValue = { it?.notes },
            setValue = { notes = it }
        )

        @JvmField
        val RATING = RecordProperty(
            id = "rating",
            name = i18n("record.property.rating"),
            typeClassReference = Int::class.java,
            getValue = { it?.rating },
            setValue = { rating = it }
        )

        @JvmField
        val SUBTITLE = RecordProperty(
            id = "subtitle",
            name = i18n("record.property.subtitle"),
            typeClassReference = String::class.java,
            getValue = { it?.subtitle },
            setValue = { subtitle = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val ISBN = RecordProperty(
            id = "isbn",
            name = i18n("record.property.isbn"),
            typeClassReference = String::class.java,
            getValue = { it?.isbn },
            setValue = { isbn = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val NUMBER_OF_COPIES =
            RecordProperty(
                id = "numberOfCopies",
                name = i18n("record.property.nofcopies"),
                typeClassReference = Int::class.javaObjectType,
                getValue = { it?.numberOfCopies },
                setValue = { numberOfCopies = it },
                typeScopes = arrayOf(Record.Type.BOOK)
            )

        @JvmField
        val AUTHORS = RecordProperty(
            id = "authors",
            name = i18n("record.property.authors"),
            typeClassReference = List::class.java as Class<List<String>>,
            getValue = { it?.authors },
            setValue = { authors = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val SUBJECT = RecordProperty(
            id = "subject",
            name = i18n("record.property.subject"),
            typeClassReference = String::class.java,
            getValue = { it?.subject },
            setValue = { subject = it },
            typeScopes = arrayOf(Record.Type.BOOK)
        )

        @JvmField
        val MAGAZINE_NAME =
            RecordProperty(
                id = "magazineName",
                name = i18n("record.property.magazinename"),
                typeClassReference = String::class.java,
                getValue = { it?.magazineName },
                setValue = { magazineName = it },
                typeScopes = arrayOf(Record.Type.MAGAZINE)
            )

        @JvmField
        val SERVICE_CONNECTION =
            RecordProperty(
                id = "serviceConnection",
                name = i18n("record.property.service_connection"),
                typeClassReference = ServiceConnection::class.java,
                getValue = { it?.serviceConnection },
                setValue = { throw UnsupportedOperationException() }
            )

        val allProperties = listOf(
            TYPE,
            AUTHORS,
            TITLE,
            SUBTITLE,
            MAGAZINE_NAME,
            ISBN,
            PUBLISHER,
            PUBLISHED_DATE,
            LANGUAGE,
            RATING,
            NUMBER_OF_COPIES,
            SUBJECT,
            SERVICE_CONNECTION,
            NOTES
        )

        val sortableProperties: List<RecordProperty<Comparable<*>>>
            get() = allProperties
                .filter(RecordProperty<*>::isSortable)
                .filterIsInstance<RecordProperty<Comparable<*>>>()
    }
}