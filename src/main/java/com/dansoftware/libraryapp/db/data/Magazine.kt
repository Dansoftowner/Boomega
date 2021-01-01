package com.dansoftware.libraryapp.db.data

import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import java.util.*

/**
 * Represents a Magazine/Newspaper record in the database.
 *
 * @author Daniel Gyorffy
 */
data class Magazine(
    @field:Id val id: NitriteId,
    var publisher: String,
    var magazineName: String,
    var title: String,
    var publishedDate: Date
) {
    var serviceConnection: ServiceConnection? = null
}