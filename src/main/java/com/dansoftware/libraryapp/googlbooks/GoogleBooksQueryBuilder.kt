package com.dansoftware.libraryapp.googlbooks

import com.dansoftware.libraryapp.util.Entry
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URIBuilder
import java.net.URISyntaxException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Stream

/**
 * Used for building queries that are capable to download data
 * from the Google Books Database.
 *
 * @author Daniel Gyorffy
 */
class GoogleBooksQueryBuilder {
    companion object {
        private const val BASE_URL = "https://www.googleapis.com/books/v1/volumes"

        // Query param flags
        private const val TITLE_FLAG = "intitle:"
        private const val AUTHOR_FLAG = "inauthor:"
        private const val PUBLISHER_FLAG = "inpublisher:"
        private const val SUBJECT_FLAG = "subject:"
        private const val ISBN_FLAG = "isbn:"

        //pagination parameters
        private const val START_INDEX = "startIndex"
        private const val MAX_RESULTS = "maxResults"
        private const val START_INDEX_DEFAULT = 0
        private const val MAX_RESULTS_DEFAULT = 10

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

        @JvmStatic
        private fun encode(value: String): String {
            return URLEncoder.encode(value, StandardCharsets.UTF_8)
        }

        @JvmStatic
        fun byId(id: String): SingleGoogleBookQuery {
            return SingleGoogleBookQuery(String.format("%s/%s", BASE_URL, id))
        }
    }

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

    fun inTitle(inTitle: String?) = this.also { it.inTitle = inTitle }

    fun inAuthor(inAuthor: String?) = this.also { it.inAuthor = inAuthor }

    fun inPublisher(inPublisher: String?) = this.also { it.inPublisher = inPublisher }

    fun subject(subject: String?) = this.also { it.subject = subject }

    fun isbn(isbn: String?) = this.also { it.isbn = isbn }

    fun startIndex(startIndex: Int) = this.also {
        require(startIndex >= 0) { "Start index can't be less than 0!" }
        it.startIndex = startIndex
    }

    fun maxResults(maxResults: Int) = this.also {
        require(maxResults <= 40) { "MaxResults can't be greater than 40!" }
        it.maxResults = maxResults
    }

    fun printType(printType: PrintType?) = this.also { it.printType = printType }

    fun sortType(sortType: SortType?) = this.also { it.sortType = sortType }

    fun language(lang: String?) = this.also { it.lang = lang }

    private fun buildBaseQueryUrl(): String {
        val baseQueryBuilder = StringBuilder(BASE_URL).append("?q=")
        val queryParams: MutableList<String> = LinkedList()
        Stream.of(
            Entry(TITLE_FLAG, inTitle),
            Entry(AUTHOR_FLAG, inAuthor),
            Entry(PUBLISHER_FLAG, inPublisher),
            Entry(ISBN_FLAG, isbn),
            Entry(SUBJECT_FLAG, subject)
        ).forEach { entry ->
            if (!StringUtils.isBlank(entry.value))
                queryParams.add(entry.key + encode(entry.value!!))
        }
        return baseQueryBuilder
            .append(queryParams.joinToString("+"))
            .toString()
    }

    fun build(): GoogleBooksQuery {
        return try {
            class ArgumentInfo(val key: Any, val value: Any?, val defaultValue: Any?)

            val additionalQueryBuilder = URIBuilder(buildBaseQueryUrl())
            Stream.of(
                ArgumentInfo(START_INDEX, startIndex, START_INDEX_DEFAULT),
                ArgumentInfo(MAX_RESULTS, maxResults, MAX_RESULTS_DEFAULT),
                ArgumentInfo(PRINT_TYPE, printType?.value, null),
                ArgumentInfo(ORDER_BY, sortType?.value, null),
                ArgumentInfo(LANG_RESTRICT, lang, null)
            ).forEach { argumentInfo: ArgumentInfo ->
                if (argumentInfo.value !== argumentInfo.defaultValue) {
                    additionalQueryBuilder.addParameter(argumentInfo.key.toString(), argumentInfo.value.toString())
                }
            }

            GoogleBooksQuery(additionalQueryBuilder.toString())
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    enum class PrintType(val value: String) {
        ALL(GoogleBooksQueryBuilder.ALL), BOOKS(GoogleBooksQueryBuilder.BOOKS), MAGAZINES(GoogleBooksQueryBuilder.MAGAZINES);
    }

    enum class SortType(val value: String) {
        REVELANCE(RELEVANCE), NEWEST(GoogleBooksQueryBuilder.NEWEST);
    }
}