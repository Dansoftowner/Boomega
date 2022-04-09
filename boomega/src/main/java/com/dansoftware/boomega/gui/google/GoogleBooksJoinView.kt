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

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.beans.binding.Bindings
import javafx.scene.control.*

class GoogleBooksJoinView(
    private val context: Context,
    record: Record,
    private val onVolumeSelected: (Volume) -> Unit
) : GoogleBooksSearchView(context) {

    init {
        fillForm(record)
    }

    private fun fillForm(record: Record) {
        searchForm.apply {
            title.value = record.title ?: ""
            author.value = record.authors?.joinToString(", ") ?: ""
            isbn.value = record.isbn ?: ""
            language.value = record.language?.language ?: ""
            publisher.value = record.publisher ?: ""
            subject.value = record.subject ?: ""
        }
    }

    override fun buildSearchResultView(query: GoogleBooksQuery): GoogleBooksSearchResultView {
        return GoogleBooksSearchResultView(context, query, onPreviousPageRequested = { home() }).apply {
            expandToolbar()
            expandTableContextMenu()
        }
    }

    private fun GoogleBooksSearchResultView.expandToolbar() {
        toolbar.rightItems.add(0, Separator())
        toolbar.rightItems.add(0, buildJoinButton())
    }

    private fun GoogleBooksSearchResultView.expandTableContextMenu() {
        pagination.table.rowContextMenu.items.add(
            MenuItem(i18n("google.books.volume_join"), icon("link-icon")).apply {
                setOnAction { onVolumeSelected(pagination.table.selectionModel.selectedItem) }
            }
        )
    }

    private fun GoogleBooksSearchResultView.buildJoinButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("link-icon")
        tooltip = Tooltip(i18n("google.books.volume_join"))
        disableProperty().bind(Bindings.isEmpty(pagination.table.selectionModel.selectedItems))
        setOnAction { onVolumeSelected(pagination.table.selectionModel.selectedItem) }
    }


}