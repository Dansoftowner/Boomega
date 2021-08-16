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
import com.dansoftware.boomega.gui.google.details.GoogleBookDetailsOverlay
import com.dansoftware.boomega.gui.util.I18NButtonTypes
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.GoogleBooksQuery
import com.dansoftware.boomega.service.googlebooks.Volume
import com.dansoftware.boomega.service.googlebooks.Volumes
import com.dansoftware.boomega.util.concurrent.CachedExecutor
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.ceil

class GoogleBooksPagination(private val context: Context, private val query: GoogleBooksQuery) : Pagination() {

    val table: GoogleBooksTable = buildTable()
    val totalItemsProperty: IntegerProperty = SimpleIntegerProperty()

    init {
        styleClass.add("google-books-pagination")
        setPageFactory {
            CachedExecutor.submit(SearchTask(query.apply { startIndex = it * query.maxResults }))
            table
        }
    }

    fun refresh() {
        CachedExecutor.submit(SearchTask(query))
    }

    private fun buildTable() = GoogleBooksTable(0).apply {
        buildDefaultColumns()
        rowContextMenu = ContextMenu(
            MenuItem(
                i18n("google.books.detail.title"),
                icon("info-outline-icon")
            ).apply {
                setOnAction { showSelectedVolumeInfo() }
            }
        )
        setOnItemDoubleClicked(::showVolumeInfo)
    }

    private fun showVolumeInfo(volume: Volume) {
        context.showOverlay(GoogleBookDetailsOverlay(context, volume))
    }

    fun showSelectedVolumeInfo() {
        showVolumeInfo(table.selectionModel.selectedItem)
    }

    private inner class SearchTask(private val query: GoogleBooksQuery) : GoogleBooksSearchTask(query) {
        init {
            setOnRunning { onRunning() }
            setOnFailed { onFailed(it.source.exception) }
            setOnSucceeded { onSucceeded(value) }
        }

        private fun onRunning() {
            table.items.clear()
            table.startIndex = query.startIndex
            context.showIndeterminateProgress()
        }

        private fun onFailed(e: Throwable?) {
            context.stopProgress()
            logger.error("Couldn't execute search task. ", e)
            context.showErrorDialog(
                i18n("google.books.search.failed.title"),
                i18n("google.books.search.failed.msg"),
                e as Exception?
            ) {
                when (it) {
                    I18NButtonTypes.RETRY -> refresh()
                }
            }.apply { buttonTypes.add(I18NButtonTypes.RETRY) }
        }

        private fun onSucceeded(volumes: Volumes) {
            context.stopProgress()
            table.items.setAll(volumes.items ?: emptyList())
            table.refresh()
            when (pageCount) {
                Int.MAX_VALUE -> {
                    pageCount = ceil(volumes.totalItems.toDouble() / query.maxResults).toInt()
                    totalItemsProperty.set(volumes.totalItems)
                }
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBooksPagination::class.java)
    }
}