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

package com.dansoftware.boomega.gui.clipboard

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.recordview.RecordTable
import javafx.collections.ListChangeListener
import jfxtras.styles.jmetro.JMetroStyleClass

class ClipboardTable : RecordTable(0) {

    private val clipboardItemsListener = ListChangeListener<Record> { showClipboardContent() }

    init {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        buildColumns()
        showClipboardContent()
        RecordClipboard.items().addListener(clipboardItemsListener)
    }

    private fun buildColumns() {
        addColumnTypes(
            TYPE_INDICATOR_COLUMN,
            AUTHOR_COLUMN,
            TITLE_COLUMN,
            SUB_TITLE_COLUMN,
            ISBN_COLUMN,
            PUBLISHER_COLUMN,
            DATE_COLUMN,
            LANG_COLUMN,
            SERVICE_CONNECTION_COLUMN
        )
    }

    private fun showClipboardContent() {
        items.setAll(RecordClipboard.items())
    }

    fun releaseListeners() {
        RecordClipboard.items().removeListener(clipboardItemsListener)
    }
}