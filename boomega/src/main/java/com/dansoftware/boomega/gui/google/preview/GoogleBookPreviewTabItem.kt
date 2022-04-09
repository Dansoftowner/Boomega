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

package com.dansoftware.boomega.gui.google.preview

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.databaseview.TabItem
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.I18N
import com.dansoftware.boomega.service.googlebooks.Volume
import javafx.scene.Node
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The [TabItem] for the Google Book preview page.
 */
class GoogleBookPreviewTabItem(val context: Context, val volume: Volume) : TabItem(
    "google_book_preview${volume.id}",
    I18N.getValue("google.book.preview.tab.title", volume.volumeInfo?.title?.let { "- $it" } ?: "")
) {

    override val graphic: Node
        get() = icon("book-preview-icon")

    override val content: Node
        get() = GoogleBookPreview(context, volume)

    override fun onClose(content: Node): Boolean {
        logger.debug("Closing google book preview tab...")
        (content as GoogleBookPreview).clean()
        return true
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GoogleBookPreviewTabItem::class.java)
    }
}