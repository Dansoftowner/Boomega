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

package com.dansoftware.boomega.gui.google.preview

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.google.details.GoogleBookDetailsOverlay
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.util.surrounding
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.scene.control.*

class GoogleBookPreviewToolbar(private val context: Context, private val view: GoogleBookPreview) : BiToolBar() {

    init {
        styleClass.add("google-book-preview-toolbar")
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildLogo())
        leftItems.add(buildLabel())
        rightItems.add(buildInfoItem())
        rightItems.add(Separator())
        rightItems.add(buildRefreshButton())
    }

    private fun buildLogo() =
        MaterialDesignIconView(MaterialDesignIcon.GOOGLE).apply {
            styleClass.add("google-icon")
        }

    private fun buildLabel() =
        Label(I18N.getValue("google.book.preview.tab.title", "- ${view.volume.volumeInfo?.title?.surrounding("\"")}"))

    private fun buildInfoItem() =
        Button().apply {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = MaterialDesignIconView(MaterialDesignIcon.INFORMATION_OUTLINE)
            tooltip = Tooltip(i18n("google.books.volume_information"))
            setOnAction {
                context.showOverlay(GoogleBookDetailsOverlay(context, view.volume))
            }
        }

    private fun buildRefreshButton() =
        Button().apply {
            contentDisplay = ContentDisplay.GRAPHIC_ONLY
            graphic = MaterialDesignIconView(MaterialDesignIcon.RELOAD)
            tooltip = Tooltip(i18n("page.reload"))
            setOnAction {
                view.reload()
            }
        }
}