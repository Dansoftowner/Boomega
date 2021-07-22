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
import javafx.scene.text.Font
import javafx.scene.text.Text

open class ReadOnlyRating(max: Int, value: Int) : Text() {

    private val maxProperty: IntegerProperty = object : SimpleIntegerProperty(max) {
        override fun invalidated() {
            update()
        }
    }

    private val ratingProperty: IntegerProperty = object : SimpleIntegerProperty(value) {
        override fun invalidated() {
            update()
        }
    }

    init {
        require(max >= value)
        styleClass.add("read-only-rating")
        style = "-fx-font-family: 'Material Design Icons' !important;"
        update()
    }

    private fun update() {
        text = MaterialDesignIcon.STAR.unicode().repeat(ratingProperty.value) +
                MaterialDesignIcon.STAR_OUTLINE.unicode().repeat(maxProperty.value - ratingProperty.value)
    }

    fun maxProperty(): IntegerProperty {
        return maxProperty
    }

    fun ratingProperty(): IntegerProperty {
        return ratingProperty
    }
}