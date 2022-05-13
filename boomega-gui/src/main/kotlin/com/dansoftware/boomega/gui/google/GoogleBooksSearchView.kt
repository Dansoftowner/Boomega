/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
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

package com.dansoftware.boomega.gui.google

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.rest.google.books.GoogleBooksQuery
import javafx.scene.Node
import javafx.scene.layout.StackPane

/**
 * The [GoogleBooksSearchView] allows to use the [GoogleBooksSearchForm] and then to view
 * the search results in a [GoogleBooksSearchResultView].
 */
open class GoogleBooksSearchView(private val context: Context) : StackPane() {

    protected val searchForm: GoogleBooksSearchForm = buildSearchForm()

    private var content: Node?
        get() = children[0]
        set(value) {
            children.setAll(value)
        }

    init {
        styleClass.add("google-books-search-view")
        buildUI()
    }

    private fun buildUI() {
        content = searchForm
    }

    private fun buildSearchForm() = GoogleBooksSearchForm(context) {
        when {
            it.isComplete() ->
                content = buildSearchResultView(it)
            else ->
                context.showErrorDialog(
                    i18n("google.books.search.incomplete.title"),
                    i18n("google.books.search.incomplete.msg")
                ) { }
        }
    }

    protected fun home() {
        content = searchForm
    }

    open fun buildSearchResultView(query: GoogleBooksQuery): GoogleBooksSearchResultView {
        return GoogleBooksSearchResultView(context, query) { home() }
    }
}