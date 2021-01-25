package com.dansoftware.libraryapp.db.data

import org.apache.commons.lang3.StringUtils
import org.dizitart.no2.IndexType
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A Book is a Plain Java Object that represents a *document* or a *record* in the database.
 *
 * Compatible with the *Nitrite* database api.
 */
@Indices(Index(value = "isbn", type = IndexType.Unique))
data class Book(
    @field:Id var id: NitriteId? = null,
    var publishedDate: String?,
    var numberOfCopies: Int?,
    var numberOfPages: Int?,
    var authors: List<String>?,
    var title: String?,
    var subtitle: String?,
    var language: String?,
    var notes: String?,
    var isbn: String?,
    var publisher: String?,
    var subject: String?,
    var rating: Int?
) {
    var serviceConnection: ServiceConnection? = null

    private constructor(builder: Builder) : this(
        null,
        builder.publishedDate,
        builder.numberOfCopies,
        builder.numberOfPages,
        builder.authors,
        builder.title,
        builder.subtitle,
        builder.language,
        builder.notes,
        builder.isbn,
        builder.publisher,
        builder.subject,
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
        null,
        null,
        null,
        null,
        null,
        null
    )

    override fun toString(): String {
        return "Book{id=$id, authors=$authors, title='$title', isbn='$isbn'}"
    }

    class Builder {
        var publishedDate: String? = null
            private set
        var numberOfPages = 0
            private set
        var numberOfCopies = 0
            private set
        var title: String? = null
            private set
        var subtitle: String? = null
            private set
        var language: String? = null
            private set
        var notes: String? = null
            private set
        var isbn: String? = null
            private set
        var publisher: String? = null
            private set
        var subject: String? = null
            private set
        var authors: List<String>? = null
            private set
        var rating: Int? = null
            private set
        var serviceConnection: ServiceConnection? = null
            private set

        fun publishedDate(publishedYear: String?) = this.also {
            this.publishedDate = StringUtils.getIfBlank(publishedYear, null)
        }

        fun publishedDate(publishedDate: LocalDate?) = this.also {
            this.publishedDate = publishedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }

        fun numberOfPages(numberOfPages: Int) = this.also {
            this.numberOfPages = numberOfPages
        }

        fun numberOfCopies(numberOfCopies: Int) = this.also {
            this.numberOfCopies = numberOfCopies
        }

        fun title(title: String?) = this.also {
            this.title = StringUtils.getIfBlank(title, null)
        }

        fun subtitle(subtitle: String?) = this.also {
            this.subtitle = StringUtils.getIfBlank(subtitle, null)
        }

        fun language(language: String?) = this.also {
            this.language = StringUtils.getIfBlank(language, null)
        }

        fun notes(notes: String?) = this.also {
            this.notes = StringUtils.getIfBlank(notes, null)
        }

        fun isbn(isbn: String?) = this.also {
            this.isbn = StringUtils.getIfBlank(isbn, null)
        }

        fun publisher(publisher: String?) = this.also {
            this.publisher = StringUtils.getIfBlank(publisher, null)
        }

        fun subject(subject: String?) = this.also {
            this.subject = StringUtils.getIfBlank(subject, null)
        }

        fun authors(authors: List<String>?) = this.also {
            this.authors = authors?.filter { it.isNotEmpty() }
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

        fun build() = Book(this)
    }
}