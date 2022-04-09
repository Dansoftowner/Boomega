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

package com.dansoftware.boomega.gui.recordview.connection

import com.dansoftware.boomega.database.api.data.Record
import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.TabItem
import com.dansoftware.boomega.gui.google.GoogleBooksJoinView
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.scene.Node

class GoogleBookJoinTab(
    private val context: Context,
    private val record: Record,
    private val onVolumeSelected: (GoogleBookJoinTab, Record, Volume) -> Unit
) : TabItem(
    "google_book_join${record.id}",
    i18n("google.book.join.tab.title")
) {
    override val graphic: Node
        get() = icon("link-icon")

    override val content: Node
        get() = GoogleBooksJoinView(context, record) {
            onVolumeSelected(this, record, it)
        }
}