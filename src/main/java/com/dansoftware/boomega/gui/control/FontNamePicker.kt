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

import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.text.Font

class FontNamePicker() : ComboBox<String>() {
    init {
        items.addAll(Font.getFontNames().distinct())
        buttonCell = FontCell()
        setCellFactory { FontCell() }
    }

    private class FontCell : ListCell<String>() {
        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty) {
                graphic = null
                text = null
            } else {
                graphic = Label(item).apply {
                    style = "-fx-font-family: $item !important;"
                }
            }
        }
    }
}