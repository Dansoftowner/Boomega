package com.dansoftware.libraryapp.db.data

import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id

/**
 * Represents a Magazine/Newspaper record in the database.
 *
 * @author Daniel Gyorffy
 */
data class Magazine(
    @field:Id val id: NitriteId,
    var publisher: String?,
    var magazineName: String?,
    var title: String?,
    var publishedDate: String?,
    var notes: String?,
    var rating: Int
) {
    var serviceConnection: ServiceConnection? = null
}