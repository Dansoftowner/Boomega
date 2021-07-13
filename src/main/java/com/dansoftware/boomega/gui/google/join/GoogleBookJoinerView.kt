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

package com.dansoftware.boomega.gui.google.join

import com.dansoftware.boomega.gui.api.Context
import com.dansoftware.boomega.gui.google.GoogleBooksPagination
import com.dansoftware.boomega.service.googlebooks.Volume
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import jfxtras.styles.jmetro.JMetroStyleClass
import java.util.function.Consumer

@Deprecated("")
class GoogleBookJoinerView(
    private val context: Context,
    private val hideMethod: (GoogleBookJoinerView) -> Unit
) : VBox(5.0) {

    private val onVolumeSelected: ObjectProperty<Consumer<Volume>> = SimpleObjectProperty()

    private val searchField: TextField = buildSearchField()
    private val tablePagination: GoogleBooksPagination = buildTable()

    init {
        styleClass.add("google-books-joiner-panel")
        styleClass.add(JMetroStyleClass.BACKGROUND)
        setVgrow(this, Priority.ALWAYS)
        buildUI()
    }

    private fun buildUI() {
        children.add(buildSearchBox())
        children.add(tablePagination)
    }

    private fun buildSearchBox() = HBox().apply {
        children.add(searchField)
        children.add(buildSearchButton())
    }

    private fun buildSearchField() = TextField().apply {
        prefHeight = 32.0
        HBox.setHgrow(this, Priority.ALWAYS)
        //TODO: prompt text
    }

    private fun buildSearchButton() = Button().apply {
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        graphic = FontAwesomeIconView(FontAwesomeIcon.SEARCH)
        prefHeight = 32.0
        isDefaultButton = true
        setOnAction { search() }
        //todo: TOOLTIP
    }

    private fun buildTable(): GoogleBooksPagination = TODO()
        /*GoogleBooksPagination().also {
            VBox.setVgrow(it, Priority.ALWAYS)
            it.table.setOnItemDoubleClicked { volume ->
                onVolumeSelected.get()?.accept(volume)
                hideMethod(this)
            }
            it.table.setOnItemSecondaryDoubleClicked { volume ->
                context.showOverlay(
                    GoogleBookDetailsOverlay(
                        context,
                        volume
                    )
                )
            }
            it.table.addColumnTypes(
                GoogleBooksTable.INDEX_COLUMN,
                GoogleBooksTable.TYPE_INDICATOR_COLUMN,
                GoogleBooksTable.THUMBNAIL_COLUMN,
                GoogleBooksTable.AUTHOR_COLUMN,
                GoogleBooksTable.TITLE_COLUMN,
            )
        }*/

    private fun search() {
        TODO()
        /*CachedExecutor.submit(
            GoogleBooksPaginationSearchTask(context, tablePagination, true, SearchParameters().inText(searchField.text))
        )*/
    }

    fun setOnVolumeSelected(onVolumeSelected: Consumer<Volume>) {
        this.onVolumeSelected.set(onVolumeSelected)
    }
}


