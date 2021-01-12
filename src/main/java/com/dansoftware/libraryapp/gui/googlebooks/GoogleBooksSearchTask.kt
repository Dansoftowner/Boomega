package com.dansoftware.libraryapp.gui.googlebooks

import com.dansoftware.libraryapp.googlebooks.GoogleBooksQueryBuilder
import com.dansoftware.libraryapp.googlebooks.Volume
import com.dansoftware.libraryapp.googlebooks.Volumes
import com.dansoftware.libraryapp.gui.context.Context
import com.dansoftware.libraryapp.locale.I18N
import com.dansoftware.libraryapp.util.ExploitativeExecutor
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.concurrent.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

/**
 * Used for specifying the search arguments for a [GoogleBooksSearchTask].
 *
 * @author Daniel Gyorffy
 */
class SearchParameters {

    var sortType: GoogleBooksQueryBuilder.SortType? = null
    var printType: GoogleBooksQueryBuilder.PrintType? = null

    var title: String? = null
        private set
    var authors: String? = null
        private set
    var publisher: String? = null
        private set
    var isbn: String? = null
        private set
    var language: String? = null
        private set
    var subject: String? = null
        private set

    var inText: String? = null
        private set

    var startIndex: Int = 0
    var maxResults: Int = 10

    fun startIndex(startIndex: Int) = this.also {
        it.startIndex = startIndex
    }

    fun maxResults(maxResults: Int) = this.also {
        it.maxResults = maxResults
    }

    fun sortType(sortType: GoogleBooksQueryBuilder.SortType?) = this.also {
        it.sortType = sortType
    }

    fun printType(printType: GoogleBooksQueryBuilder.PrintType?) = this.also {
        it.printType = printType
    }

    fun inText(inText: String?) = this.also {
        it.inText = inText
    }

    fun title(title: String?) = this.also {
        it.title = title
    }

    fun authors(authors: String?) = this.also {
        it.authors = authors
    }

    fun subject(subject: String?) = this.also {
        it.subject = subject
    }

    fun publisher(publisher: String?) = this.also {
        it.publisher = publisher
    }

    fun language(language: String?) = this.also {
        it.language = language
    }

    fun isbn(isbn: String?) = this.also {
        it.isbn = isbn
    }

    fun copy(): SearchParameters = SearchParameters()
        .startIndex(this.startIndex)
        .maxResults(this.maxResults)
        .sortType(this.sortType)
        .printType(this.printType)
        .inText(this.inText)
        .title(this.title)
        .authors(this.authors)
        .subject(this.subject)
        .publisher(this.publisher)
        .language(this.language)
        .isbn(this.isbn)
}

/**
 * A JavaFX [Task] used for loading a Google Books query result.
 */
open class GoogleBooksSearchTask(private val searchParameters: SearchParameters) : Task<Volumes>() {
    override fun call(): Volumes =
        GoogleBooksQueryBuilder()
            .inText(searchParameters.inText)
            .inAuthor(searchParameters.authors)
            .inTitle(searchParameters.title)
            .inPublisher(searchParameters.publisher)
            .subject(searchParameters.subject)
            .isbn(searchParameters.isbn)
            .language(searchParameters.language)
            .printType(searchParameters.printType)
            .sortType(searchParameters.sortType)
            .maxResults(searchParameters.maxResults)
            .startIndex(searchParameters.startIndex)
            .build().load()
}

/**
 * A [GoogleBooksSearchTask] that is used for loading into a [GoogleBooksPagination].
 *
 * @param context the [Context] object
 * @param tablePagination the [GoogleBooksPagination]
 * @param isInitSearch boolean value that indicates that the task is used for an initial search, or
 * for a reload search
 */
class GoogleBooksPaginationSearchTask(
    private val context: Context,
    private val tablePagination: GoogleBooksPagination,
    private val isInitSearch: Boolean,
    searchParameters: SearchParameters
) : GoogleBooksSearchTask(searchParameters) {

    companion object {
        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(GoogleBooksSearchTask::class.java)
    }

    private val onBeforeResultsDisplayed: ObjectProperty<Runnable> = SimpleObjectProperty()
    private val onNewContentRequestCreated: ObjectProperty<Consumer<Runnable>> = SimpleObjectProperty()

    init {
        setOnRunning { onRunning() }
        setOnFailed { onFailed(it.source.exception) }
        setOnSucceeded { postSearch(this.value, searchParameters.copy()) }
    }

    private fun onRunning() {
        context.showIndeterminateProgress()
    }

    private fun onFailed(cause: Throwable) {
        context.stopProgress()
        logger.error("Search problem ", cause)
        context.showErrorDialog(
            I18N.getGoogleBooksImportValue("google.books.search.failed.title"),
            I18N.getGoogleBooksImportValue("google.books.search.failed.msg"),
            cause as Exception
        ) { }
    }

    private fun postSearch(volumes: Volumes, searchParameters: SearchParameters) {
        context.stopProgress()
        onBeforeResultsDisplayed.get()?.run()
        logger.debug("Total items: {}", volumes.totalItems)

        if (isInitSearch) {
            tablePagination.clear()
            tablePagination.itemsPerPage = searchParameters.maxResults
            tablePagination.totalItems = volumes.totalItems
        }

        val items: List<Volume>? = volumes.items
        when {
            items?.isEmpty() ?: true -> tablePagination.clear()
            else -> {
                tablePagination.setOnNewContentRequest { start, size ->
                    Runnable {
                        ExploitativeExecutor.submit(
                            GoogleBooksPaginationSearchTask(
                                context,
                                tablePagination,
                                false,
                                searchParameters.startIndex(start)
                            ).also {
                                it.setOnNewContentRequestCreated(onNewContentRequestCreated.get())
                                it.setOnBeforeResultsDisplayed(onBeforeResultsDisplayed.get())
                            }
                        )
                    }.also {
                        it.run()
                        tablePagination.scrollToTop()
                        onNewContentRequestCreated.get()?.accept(it)
                    }
                }
                tablePagination.setItems(items)
            }
        }
    }

    fun setOnBeforeResultsDisplayed(action: Runnable) {
        this.onBeforeResultsDisplayed.set(action)
    }

    fun setOnNewContentRequestCreated(action: Consumer<Runnable>) {
        this.onNewContentRequestCreated.set(action)
    }
}