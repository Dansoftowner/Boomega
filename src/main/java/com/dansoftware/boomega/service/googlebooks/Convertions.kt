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

package com.dansoftware.boomega.service.googlebooks

import com.dansoftware.boomega.db.data.Record
import com.dansoftware.boomega.db.data.ServiceConnection
import java.util.*

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