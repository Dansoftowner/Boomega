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

import com.dansoftware.boomega.gui.control.BiToolBar
import com.dansoftware.boomega.gui.keybinding.KeyBindingTooltip
import com.dansoftware.boomega.gui.keybinding.KeyBindings
import com.dansoftware.boomega.gui.util.icon
import com.dansoftware.boomega.i18n.api.i18n
import javafx.beans.binding.Bindings
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
        styleClass.add("google-book-search-toolbar")
        buildUI()
    }

    private fun buildUI() {
        leftItems.add(buildHomeButton())
        leftItems.add(Separator())
        leftItems.add(buildTotalItemsLabel())
        rightItems.add(buildVolumeInfoButton())
        rightItems.add(Separator())
        rightItems.add(buildReloadButton())
    }

    private fun buildHomeButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("arrow-left-icon")
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

    private fun buildVolumeInfoButton() = Button().apply {
        disableProperty().bind(Bindings.isEmpty(view.pagination.table.selectionModel.selectedItems))
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("info-outline-icon")
        tooltip = Tooltip(i18n("google.books.detail.title"))
        setOnAction { view.showSelectedVolumeInfo() }
    }

    private fun buildReloadButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = icon("reload-icon")
        tooltip = KeyBindingTooltip(i18n("page.reload"), KeyBindings.refreshPage)
        setOnAction { view.refresh() }
    }
}
