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
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.util.addKeyBindingDetection
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import javafx.beans.property.IntegerProperty
import javafx.scene.layout.BorderPane

/**
 * The view that shows the Google Books search results.
 *
 * Consists of a [GoogleBooksSearchToolbar] and a [GoogleBooksPagination].
 */
class GoogleBooksSearchResultView(context: Context, query: GoogleBooksQuery, onPreviousPageRequested: () -> Unit) :
    BorderPane() {

    val pagination = GoogleBooksPagination(context, query)
    val totalItemsProperty: IntegerProperty
        get() = pagination.totalItemsProperty

    val toolbar = GoogleBooksSearchToolbar(this, onPreviousPageRequested)

    init {
        styleClass.add("google-books-search-result-view")
        initKeyBindDetections()
        buildUI()
    }

    private fun buildUI() {
        top = toolbar
        center = pagination
    }

    private fun initKeyBindDetections() {
        addKeyBindingDetection(KeyBindings.refreshPage) { refresh() }
    }

    fun refresh() {
        pagination.refresh()
    }

    fun showSelectedVolumeInfo() {
        pagination.showSelectedVolumeInfo()
    }

}