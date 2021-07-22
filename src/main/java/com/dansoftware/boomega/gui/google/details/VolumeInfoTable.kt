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

package com.dansoftware.boomega.gui.google.details

import com.dansoftware.boomega.gui.control.FixedFontMaterialDesignIconView
import com.dansoftware.boomega.gui.control.HighlightableLabel
import com.dansoftware.boomega.gui.control.PropertyTable
import com.dansoftware.boomega.gui.control.ReadOnlyRating
import com.dansoftware.boomega.i18n.I18N
import com.dansoftware.boomega.i18n.i18n
import com.dansoftware.boomega.service.googlebooks.Volume
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.*
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import java.util.*

class VolumeInfoTable(volume: ObjectProperty<Volume>) : PropertyTable() {

    private val type: ObjectProperty<String> = SimpleObjectProperty()
    private val title: StringProperty = SimpleStringProperty()
    private val subtitle: StringProperty = SimpleStringProperty()
    private val authors: StringProperty = SimpleStringProperty()
    private val publisher: StringProperty = SimpleStringProperty()
    private val date: StringProperty = SimpleStringProperty()
    private val language: StringProperty = SimpleStringProperty()
    private val averageRating: DoubleProperty = SimpleDoubleProperty()
    private val ratingsCount: IntegerProperty = SimpleIntegerProperty()
    private val previewLink: StringProperty = SimpleStringProperty()
    private val industryIdentifiers: ObjectProperty<List<String>?> = SimpleObjectProperty()
    private val categories: ObjectProperty<List<String>?> = SimpleObjectProperty()

    init {
        volume.addListener { _, _, newVolume -> handleNewVolume(newVolume) }
        buildEntries()
    }

    private fun buildEntries() {
        items.add(Entry(i18n("google.books.prop.type"), TypeIndicator()))
        items.add(Entry(i18n("google.books.table.column.title"), title))
        items.add(Entry(i18n("google.books.table.column.subtitle"), subtitle))
        items.add(Entry(i18n("google.books.table.column.author"), authors))
        items.add(Entry(i18n("google.books.table.column.publisher"), publisher))
        items.add(Entry(i18n("google.books.table.column.date"), date))
        items.add(Entry(i18n("google.books.table.column.lang"), language))
        items.add(Entry(i18n("google.books.table.column.isbn"), IndustryIdentifiersBox()))
        items.add(Entry(i18n("google.books.categories"), CategoriesBox()))
        items.add(Entry(i18n("google.books.table.column.rank"), RatingControl()))
    }

    private fun handleNewVolume(volume: Volume?) {
        type.set(volume?.volumeInfo?.printType)
        title.set(volume?.volumeInfo?.title)
        subtitle.set(volume?.volumeInfo?.subtitle)
        authors.set(volume?.volumeInfo?.authors?.joinToString(", "))
        publisher.set(volume?.volumeInfo?.publisher)
        date.set(volume?.volumeInfo?.publishedDate)
        language.set(volume?.volumeInfo?.language?.let(Locale::forLanguageTag)?.displayLanguage)
        previewLink.set(volume?.volumeInfo?.previewLink)
        averageRating.value = volume?.volumeInfo?.averageRating
        ratingsCount.value = volume?.volumeInfo?.ratingsCount
        industryIdentifiers.set(volume?.volumeInfo?.industryIdentifiers?.map(Volume.VolumeInfo.IndustryIdentifier::toString))
        categories.set(volume?.volumeInfo?.categories)
    }

    private inner class TypeIndicator : StackPane() {
        init {
            updateUI()
            type.addListener { _, _, _ -> updateUI() }
        }

        private fun updateUI() {
            children.setAll(HBox(5.0, *when {
                type.get() == Volume.VolumeInfo.MAGAZINE -> arrayOf(
                    FixedFontMaterialDesignIconView(MaterialDesignIcon.NEWSPAPER, 17.0),
                    StackPane(Label(I18N.getValue("google.books.magazine")))
                )
                else -> arrayOf(
                    FixedFontMaterialDesignIconView(MaterialDesignIcon.BOOK, 17.0),
                    StackPane(Label(I18N.getValue("google.books.book")))
                )
            }))
        }
    }

    private inner class IndustryIdentifiersBox : VBox(2.0) {
        init {
            updateUI()
            industryIdentifiers.addListener { _, _, _ -> updateUI() }
        }

        private fun updateUI() {
            children.setAll(
                industryIdentifiers.get()?.map {
                    HBox(2.0,
                        Label("${8226.toChar()}"),
                        HighlightableLabel(it).apply {
                            HBox.setHgrow(this, Priority.ALWAYS)
                        }
                    )
                } ?: listOf(Label(" - "))
            )
        }
    }

    private inner class CategoriesBox : VBox(2.0) {
        init {
            updateUI()
            categories.addListener { _, _, _ -> updateUI() }
        }

        private fun updateUI() {
            children.setAll(categories.get()?.map {
                HBox(2.0,
                    Label("${8226.toChar()}"),
                    HighlightableLabel(it).apply {
                        HBox.setHgrow(this, Priority.ALWAYS)
                    }
                )
            } ?: listOf(Label(" - ")))
        }
    }

    private inner class RatingControl : HBox(2.0) {
        init {
            children.add(buildRating())
            children.add(buildRatingCountLabel())
        }

        private fun buildRating() = ReadOnlyRating(5, 0).apply {
            ratingProperty().bind(averageRating)
        }

        private fun buildRatingCountLabel() = Label().apply {
            textProperty().bind(SimpleStringProperty("(").concat(ratingsCount).concat(")"))
        }
    }
}