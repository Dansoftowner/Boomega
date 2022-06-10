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

@file:JvmName("VolumeRecordConversions")

package com.dansoftware.boomega.gui.google

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.database.api.data.ServiceConnection
import com.dansoftware.boomega.rest.google.books.Volume
import com.dansoftware.boomega.rest.google.books.getPublishedDateObject
import com.dansoftware.boomega.rest.google.books.isMagazine
import java.util.*

/**
 * Retrieves and allows access for the `google.book.handle` entry.
 *
 * It's intended to hold a [Volume]'s id.
 *
 * @see Volume.id
 */
var ServiceConnection.googleBookHandle: String?
    get() = this["google.book.handle"]?.toString()
    set(value) {
        this["google.book.handle"] = value
    }

/**
 * Converts a [Volume] to a Boomega-record.
 */
fun Volume.asRecord(): Record {
    return Record().apply {
        type = when {
            volumeInfo?.isMagazine ?: false -> Record.Type.MAGAZINE
            else -> Record.Type.BOOK
        }
        authors = volumeInfo?.authors
        publishedDate = volumeInfo?.getPublishedDateObject()
        isbn = volumeInfo?.industryIdentifiers?.find { it.isIsbn13 }?.identifier
        language = Locale.forLanguageTag(volumeInfo?.language)
        title = volumeInfo?.title
        subtitle = volumeInfo?.subtitle
        publisher = volumeInfo?.publisher
        serviceConnection = ServiceConnection().apply { googleBookHandle = this@asRecord.id }
    }
}