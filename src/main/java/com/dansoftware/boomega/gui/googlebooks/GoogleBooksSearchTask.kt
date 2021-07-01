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

package com.dansoftware.boomega.gui.googlebooks

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.I18NButtonTypes
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQueryBuilder
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.service.googlebooks.Volumes
import com.dansoftware.boomega.util.concurrent.CachedExecutor
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

    companion object {
        @JvmStatic
        protected val logger: Logger = LoggerFactory.getLogger(GoogleBooksSearchTask::class.java)
    }

    override fun call(): Volumes {
        return GoogleBooksQueryBuilder()
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
            .build()
            .also { logger.debug("Google books query: {}", it) }
            .let {
                when {
                    it.isEmpty -> Volumes()
                    else -> it.load()
                }
            }
    }
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
    private val searchParameters: SearchParameters
) : GoogleBooksSearchTask(searchParameters) {

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
            I18N.getValue("google.books.search.failed.title"),
            I18N.getValue("google.books.search.failed.msg"),
            cause as Exception
        ) {
            when (it) {
                I18NButtonTypes.RETRY ->
                    CachedExecutor.submit(
                        GoogleBooksPaginationSearchTask(context, tablePagination, isInitSearch, searchParameters)
                    )
            }
        }.also {
            it.buttonTypes.add(I18NButtonTypes.RETRY)
        }
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
                tablePagination.setOnNewContentRequest { start, _ ->
                    Runnable {
                        CachedExecutor.submit(
                            GoogleBooksPaginationSearchTask(
                                context,
                                tablePagination,
                                false,
                                searchParameters.startIndex(start)
                            ).also {
                                onNewContentRequestCreated.get()
                                    ?.let { value -> it.setOnNewContentRequestCreated(value) }
                                onBeforeResultsDisplayed.get()?.let { value -> it.setOnBeforeResultsDisplayed(value) }
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