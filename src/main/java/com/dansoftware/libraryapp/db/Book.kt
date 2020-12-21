package com.dansoftware.libraryapp.db

import org.dizitart.no2.IndexType
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import org.dizitart.no2.objects.Index
import org.dizitart.no2.objects.Indices
import java.util.*

/**
 * A Book is a Plain Java Object that represents a *document* or a *record* in the database.
 *
 * Compatible with the *Nitrite* database api.
 */
@Indices(Index(value = "isbn", type = IndexType.Unique))
data class Book(
    @field:Id var id: NitriteId? = null,
    var publishedYear: Int,
    var numberOfCopies: Int,
    var numberOfPages: Int,
    var authors: List<String>?,
    var title: String?,
    var language: String?,
    var notes: String?,
    var isbn: String?,
    var publisher: String?,
    var subject: String?
) {
    var googleID: String? = null

    private constructor(builder: Builder) {
        title = Objects.requireNonNull(builder.title)
        authors = Objects.requireNonNull(builder.authors)
        publishedYear = builder.publishedYear
        numberOfCopies = builder.numberOfCopies
        numberOfPages = builder.numberOfPages
        language = builder.language
        notes = builder.notes
        isbn = builder.isbn
        publisher = builder.publisher
        subject = builder.subject
    }

    override fun toString(): String {
        return "Book{id=$id, authors=$authors, title='$title', isbn='$isbn'}"
    }

    class Builder {
        var publishedYear = 0
            private set
        var numberOfPages = 0
            private set
        var numberOfCopies = 0
            private set
        var title: String? = null
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

        fun publishedYear(publishedYear: Int) = this.also { this.publishedYear = publishedYear }

        fun numberOfPages(numberOfPages: Int) = this.also { this.numberOfPages = numberOfPages }

        fun numberOfCopies(numberOfCopies: Int) = this.also { this.numberOfCopies = numberOfCopies }

        fun title(title: String?) = this.also { this.title = title }

        fun language(language: String?) = this.also { this.language = language }

        fun notes(notes: String?) = this.also { this.notes = notes }

        fun isbn(isbn: String?) = this.also { this.isbn = isbn }

        fun publisher(publisher: String?) = this.also { this.publisher = publisher }

        fun subject(subject: String?) = this.also { this.subject = subject }

        fun authors(authors: List<String>?) = this.also { this.authors = authors }

        fun build() = Book(this)
    }
}