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

package com.dansoftware.boomega.gui.google

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.DatabaseView
import com.dansoftware.boomega.gui.recordview.RecordsViewModule
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.service.googlebooks.asRecord
import javafx.beans.binding.Bindings
import javafx.scene.control.*

/**
 * A [GoogleBooksImportView] is a [GoogleBooksSearchView] that also allows the user
 * to import [Volume]s into the local database.
 */
class GoogleBooksImportView(private val context: Context) : GoogleBooksSearchView(context) {

    override fun buildSearchResultView(query: GoogleBooksQuery): GoogleBooksSearchResultView {
        return GoogleBooksSearchResultView(context, query, onPreviousPageRequested = { home() }).apply {
            expandToolbar()
            expandTableContextMenu()
        }
    }

    private fun GoogleBooksSearchResultView.expandToolbar() {
        toolbar.rightItems.add(0, Separator())
        toolbar.rightItems.add(0, buildImportButton())
    }

    private fun GoogleBooksSearchResultView.expandTableContextMenu() {
        pagination.table.rowContextMenu.items.add(
            MenuItem(i18n("google.books.volume_import"), icon("plus-icon")).apply {
                disableProperty().bind(Bindings.isEmpty(pagination.table.selectionModel.selectedItems))
                setOnAction {
                    sendInsertionRequest(pagination.table.selectionModel.selectedItem)
                }
            }
        )
    }

    private fun GoogleBooksSearchResultView.buildImportButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("plus-icon")
        tooltip = Tooltip(i18n("google.books.volume_import"))
        disableProperty().bind(Bindings.isEmpty(pagination.table.selectionModel.selectedItems))
        setOnAction { sendInsertionRequest(pagination.table.selectionModel.selectedItem) }
    }

    private fun sendInsertionRequest(volume: Volume) {
        context.sendRequest(
            DatabaseView.ModuleShowRequest(
                RecordsViewModule::class.java,
                RecordsViewModule.InsertionRequest(volume.asRecord())
            )
        )
    }
}