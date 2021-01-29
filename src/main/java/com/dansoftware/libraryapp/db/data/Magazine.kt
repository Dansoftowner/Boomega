package com.dansoftware.libraryapp.db.data

import org.apache.commons.lang3.StringUtils
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id

/**
 * Represents a Magazine/Newspaper record in the database.
 *
 * @author Daniel Gyorffy
 */
@Deprecated("")
data class Magazine(
    @field:Id val id: NitriteId?,
    var publisher: String?,
    var magazineName: String?,
    var title: String?,
    var publishedDate: String?,
    var language: String?,
    var notes: String?,
    var rating: Int?
) {
    var serviceConnection: ServiceConnection? = null
    get() = field ?: ServiceConnection()

    private constructor(builder: Builder) : this(
        null,
        builder.publisher,
        builder.magazineName,
        builder.title,
        builder.publishedDate,
        builder.language,
        builder.notes,
        builder.rating
    ) {
        this.serviceConnection = builder.serviceConnection
    }

    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )

    class Builder {
        var publisher: String? = null
            private set
        var magazineName: String? = null
            private set
        var title: String? = null
            private set
        var publishedDate: String? = null
            private set
        var notes: String? = null
            private set
        var language: String? = null
            private set
        var rating: Int? = null
            private set
        var serviceConnection: ServiceConnection? = null
            private set

        fun publisher(publisher: String?) = this.also {
            this.publisher = StringUtils.getIfBlank(publisher, null)
        }

        fun magazineName(magazineName: String?) = this.also {
            this.magazineName = StringUtils.getIfBlank(magazineName, null)
        }

        fun title(title: String?) = this.also {
            this.title = StringUtils.getIfBlank(title, null)
        }

        fun publishedDate(publishedDate: String?) = this.also {
            this.publishedDate = StringUtils.getIfBlank(publishedDate, null)
        }

        fun language(language: String?) = this.also {
            this.language = language;
        }

        fun notes(notes: String?) = this.also {
            this.notes = StringUtils.getIfBlank(notes, null)
        }

        fun rating(rating: Int?) = this.also {
            this.rating = rating
        }

        fun serviceConnection(serviceConnection: ServiceConnection?) = this.also {
            when {
                serviceConnection?.isEmpty()?.not() ?: false ->
                    this.serviceConnection = serviceConnection
            }
        }

        fun build() = Magazine(this)
    }
}