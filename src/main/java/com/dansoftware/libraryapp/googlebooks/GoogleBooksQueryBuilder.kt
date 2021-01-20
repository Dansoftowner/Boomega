package com.dansoftware.libraryapp.googlebooks

import com.dansoftware.libraryapp.util.surrounding
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URIBuilder
import java.net.URISyntaxException
import java.util.*

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
        private val SORT_TYPE_DEFAULT = SortType.REVELANCE

        @JvmStatic
        fun byId(id: String): SingleGoogleBookQuery {
            return SingleGoogleBookQuery("${BASE_URL}/${id}")
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

    fun inText(inText: String?) = this.also { it.inText = StringUtils.getIfBlank(inText, null) }

    fun inTitle(inTitle: String?) = this.also { it.inTitle = StringUtils.getIfBlank(inTitle, null) }

    fun inAuthor(inAuthor: String?) = this.also { it.inAuthor = StringUtils.getIfBlank(inAuthor, null) }

    fun inPublisher(inPublisher: String?) = this.also { it.inPublisher = StringUtils.getIfBlank(inPublisher, null) }

    fun subject(subject: String?) = this.also { it.subject = StringUtils.getIfBlank(subject, null) }

    fun isbn(isbn: String?) = this.also { it.isbn = StringUtils.getIfBlank(isbn, null) }

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

    fun language(lang: String?) = this.also { it.lang = StringUtils.getIfBlank(lang, null) }

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
            URIBuilder(BASE_URL).also { uriBuilder ->
                uriBuilder.addParameter("q", buildQueryString())
                startIndex.takeIf { it != START_INDEX_DEFAULT }
                    ?.let { uriBuilder.addParameter(START_INDEX, it.toString()) }
                maxResults.takeIf { it != MAX_RESULTS_DEFAULT }
                    ?.let { uriBuilder.addParameter(MAX_RESULTS, it.toString()) }
                printType.takeIf { it != PRINT_TYPE_DEFAULT }
                    ?.let { uriBuilder.addParameter(PRINT_TYPE, it.toString()) }
                sortType.takeIf { it != SORT_TYPE_DEFAULT }?.let { uriBuilder.addParameter(ORDER_BY, it.value) }
                lang?.let { uriBuilder.addParameter(LANG_RESTRICT, lang) }
            }.let { GoogleBooksQuery(it.toString()) }
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