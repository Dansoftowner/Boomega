package com.dansoftware.libraryapp.db.data

import com.dansoftware.libraryapp.gui.record.RecordType
import org.dizitart.no2.NitriteId
import org.dizitart.no2.objects.Id
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Record(
    @field:Id var id: NitriteId? = null,
    var recordType: Type = Type.BOOK,

    //General
    var title: String? = null,
    var language: String? = null,
    var publisher: String? = null,
    var publishedDate: String? = null,
    var notes: String? = null,
    var rating: Int?,

    //Book specific properties
    @field:BookProperty var subtitle: String? = null,
    @field:BookProperty var isbn: String? = null,
    @field:BookProperty var numberOfCopies: Int? = null,
    @field:BookProperty var numberOfPages: Int? = null,
    @field:BookProperty var authors: List<String>?,
    @field:BookProperty var subject: String? = null,

    //Magazine specific properties
    @field:MagazineProperty var magazineName: String? = null
) {

    constructor(): this(
        null,
        Type.BOOK,
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

    private constructor(builder: Builder): this(
        null,
        builder.recordType,
        builder.title,
        builder.language,
        builder.publisher,
        builder.publishedDate,
        builder.notes,
        builder.rating,
        builder.subtitle,
        builder.isbn,
        builder.numberOfCopies,
        builder.numberOfPages,
        builder.authors,
        builder.subject,
        builder.magazineName
    )

    enum class Type {
        BOOK, MAGAZINE
    }

    class Builder(
        var recordType: Type,
        var title: String? = null,
        var language: String? = null,
        var publisher: String? = null,
        var publishedDate: String? = null,
        var notes: String? = null,
        var rating: Int?,

        //Book specific properties
        @field:BookProperty var subtitle: String? = null,
        @field:BookProperty var isbn: String? = null,
        @field:BookProperty var numberOfCopies: Int? = null,
        @field:BookProperty var numberOfPages: Int? = null,
        @field:BookProperty var authors: List<String>?,
        @field:BookProperty var subject: String? = null,

        //Magazine specific properties
        @field:MagazineProperty var magazineName: String? = null
    ) {
        fun title(title: String?) = apply { this.title = title }
        fun language(language: String?) = apply { this.language = language }
        fun publisher(publisher: String?) = apply { this.publisher = publisher }
        fun publishedDate(publishedDate: LocalDate?) = apply {
            this.publishedDate = publishedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
        fun notes(notes: String?) = apply { this.notes = notes }
        fun rating(rating: Int?) = apply { this.rating = rating }

        fun subtitle(subtitle: String?) = apply { this.subtitle = subtitle }
        fun isbn(isbn: String?) = apply { this.isbn = isbn }
        fun numberOfCopies(numberOfCopies: Int?) = apply { this.numberOfCopies = numberOfCopies }
        fun numberOfPages(numberOfPages: Int?) = apply { this.numberOfPages = numberOfPages }
        fun authors(authors: List<String>?) = apply { this.authors = authors }
        fun subject(subject: String?) = apply { this.subject = subject }

        fun magazineName(magazineName: String?) = apply { this.magazineName = magazineName }

        fun build() = Record(this)
    }
}