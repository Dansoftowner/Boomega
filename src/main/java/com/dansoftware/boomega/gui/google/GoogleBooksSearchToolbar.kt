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

import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.i18n.i18n
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.*

/**
 * The toolbar used in the [GoogleBooksSearchResultView].
 */
class GoogleBooksSearchToolbar(
    private val view: GoogleBooksSearchResultView,
    private val onPreviousPageRequested: () -> Unit
) : BiToolBar() {

    init {
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildHomeButton())
        leftItems.add(Separator())
        leftItems.add(buildTotalItemsLabel())
        rightItems.add(buildReloadButton())
    }

    private fun buildHomeButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_LEFT)
        tooltip = Tooltip(i18n("google.books.search.back"))
        setOnAction { onPreviousPageRequested() }
    }

    private fun buildTotalItemsLabel() = Label().apply {
        padding = Insets(0.0, 0.0, 0.0, 5.0)
        textProperty().bind(
            SimpleStringProperty(i18n("google.books.pagination.totalitems"))
                .concat(" ")
                .concat(view.totalItemsProperty)
        )
    }

    private fun buildReloadButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = MaterialDesignIconView(MaterialDesignIcon.RELOAD)
        tooltip = Tooltip(i18n("page.reload"))
        setOnAction { view.refresh() }
    }

}
