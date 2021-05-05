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

import com.dansoftware.boomega.util.surrounding
import okhttp3.HttpUrl
import org.apache.commons.lang3.StringUtils
import java.net.URISyntaxException
import java.net.URL
import java.util.*

/**
 * Used for building queries that are capable to download data
 * from the Google Books Database.
 *
 * @author Daniel Gyorffy
 */
class GoogleBooksQueryBuilder {

    companion object {
        private const val PROTOCOL = "https"
        private const val HOST = "www.googleapis.com"
        private const val PATH_SEGMENTS = "books/v1/volumes"

        // Query param flags
        private const val TITLE_FLAG = "intitle:"
        private const val AUTHOR_FLAG = "inauthor:"
        private const val PUBLISHER_FLAG = "inpublisher:"
        private const val SUBJECT_FLAG = "subject:"
        private const val ISBN_FLAG = "isbn:"

        //pagination parameters
        private const val START_INDEX = "startIndex"
        private const val MAX_RESULTS = "maxResults"

        //restriction parameters by printType
        private const val PRINT_TYPE = "printType"
        private const val ALL = "all"

        private const val BOOKS = "books"
        private const val MAGAZINES = "magazines"

        //Sorting
        private const val ORDER_BY = "orderBy"
        private const val NEWEST = "newest"

        private const val RELEVANCE = "relevance"

        //Filtering by language
        private const val LANG_RESTRICT = "langRestrict"

        //defaults
        private const val START_INDEX_DEFAULT = 0
        private const val MAX_RESULTS_DEFAULT = 10

        @JvmStatic
        private val PRINT_TYPE_DEFAULT = PrintType.ALL

        @JvmStatic
        private val SORT_TYPE_DEFAULT = SortType.RELEVANCE

        @JvmStatic
        fun byId(id: String): SingleGoogleBookQuery {
            return when {
                id.startsWith(PROTOCOL) -> SingleGoogleBookQuery(URL(id)) // for compatibility with previous strategies
                else -> SingleGoogleBookQuery(
                    HttpUrl.Builder()
                        .scheme(PROTOCOL)
                        .host(HOST)
                        .addPathSegments(PATH_SEGMENTS)
                        .addPathSegment(id)
                        .build().toUrl()
                )
            }
        }
    }

    private var inText: String? = null
    private var inTitle: String? = null
    private var inAuthor: String? = null
    private var inPublisher: String? = null
    private var subject: String? = null
    private var isbn: String? = null
    private var lang: String? = null
    private var startIndex = 0
    private var maxResults = 10
    private var printType: PrintType? = null
    private var sortType: SortType? = null

    fun inText(inText: String?) = apply { this.inText = StringUtils.getIfBlank(inText, null) }

    fun inTitle(inTitle: String?) = apply { this.inTitle = StringUtils.getIfBlank(inTitle, null) }

    fun inAuthor(inAuthor: String?) = apply { this.inAuthor = StringUtils.getIfBlank(inAuthor, null) }

    fun inPublisher(inPublisher: String?) = apply { this.inPublisher = StringUtils.getIfBlank(inPublisher, null) }

    fun subject(subject: String?) = apply { this.subject = StringUtils.getIfBlank(subject, null) }

    fun isbn(isbn: String?) = apply { this.isbn = StringUtils.getIfBlank(isbn, null) }

    fun startIndex(startIndex: Int) = apply {
        require(startIndex >= 0) { "Start index can't be less than 0!" }
        this.startIndex = startIndex
    }

    fun maxResults(maxResults: Int) = apply {
        require(maxResults <= 40) { "MaxResults can't be greater than 40!" }
        this.maxResults = maxResults
    }

    fun printType(printType: PrintType?) = apply { this.printType = printType }

    fun sortType(sortType: SortType?) = apply { this.sortType = sortType }

    fun language(lang: String?) = apply { this.lang = StringUtils.getIfBlank(lang, null) }

    private fun buildQueryString(): String {
        return LinkedList<String>().also { members ->
            inText?.let { members.add(it) }
            inTitle?.let { members.add(TITLE_FLAG + it.surrounding("\"")) }
            inAuthor?.let { members.add(AUTHOR_FLAG + it.surrounding("\"")) }
            inPublisher?.let { members.add(PUBLISHER_FLAG + it.surrounding("\"")) }
            isbn?.let { members.add(ISBN_FLAG + it) }
            subject?.let { members.add(SUBJECT_FLAG + it.surrounding("\"")) }
        }.joinToString(" ")
    }

    fun build(): GoogleBooksQuery {
        return try {
            GoogleBooksQuery(
                HttpUrl.Builder()
                    .scheme(PROTOCOL)
                    .host(HOST)
                    .addPathSegments(PATH_SEGMENTS)
                    .addQueryParameter("q", buildQueryString())
                    .addQueryParameter(START_INDEX, startIndex.toString())
                    .addQueryParameter(MAX_RESULTS, maxResults.toString())
                    .addQueryParameter(PRINT_TYPE, (printType ?: PRINT_TYPE_DEFAULT).toString())
                    .addQueryParameter(ORDER_BY, (sortType ?: SORT_TYPE_DEFAULT).toString())
                    .addQueryParameter(LANG_RESTRICT, lang)
                    .build().toUrl()
            )
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    enum class PrintType(val value: String) {
        ALL(GoogleBooksQueryBuilder.ALL), BOOKS(GoogleBooksQueryBuilder.BOOKS), MAGAZINES(GoogleBooksQueryBuilder.MAGAZINES);
    }

    enum class SortType(val value: String) {
        RELEVANCE(GoogleBooksQueryBuilder.RELEVANCE), NEWEST(GoogleBooksQueryBuilder.NEWEST);
    }
}