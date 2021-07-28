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

package com.dansoftware.boomega.gui.control

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.HBox
import javafx.scene.text.Text

open class ReadOnlyRating(max: Int, value: Int) : HBox() {

    @get:JvmName("maxProperty")
    val maxProperty: IntegerProperty = object : SimpleIntegerProperty(max) {
        override fun invalidated() {
            update()
        }
    }

    @get:JvmName("ratingProperty")
    val ratingProperty: IntegerProperty = object : SimpleIntegerProperty(value) {
        override fun invalidated() {
            update()
        }
    }

    var rating: Int
        get() = ratingProperty.get()
        set(value) {
            ratingProperty.set(value)
        }

    var max: Int
        get() = maxProperty.get()
        set(value) {
            maxProperty.set(value)
        }

    init {
        require(max >= value)
        styleClass.add("read-only-rating")
        update()
    }

    private fun update() {
        children.setAll(filledStars() + unFilledStars())
    }

    private fun filledStars() =
        (1..ratingProperty.value).map { filledStarIcon() }

    private fun unFilledStars() =
        (ratingProperty.value until maxProperty.value).map { unFilledStarIcon() }

    private fun filledStarIcon() =
        starIcon("filled-star")

    private fun unFilledStarIcon() =
        starIcon("unfilled-star")

    private fun starIcon(styleClass: String) =
        Text(MaterialDesignIcon.STAR.unicode()).apply {
            this.styleClass.add(styleClass)
            this.style = "-fx-font-family: 'Material Design Icons' !important;"
        }

}