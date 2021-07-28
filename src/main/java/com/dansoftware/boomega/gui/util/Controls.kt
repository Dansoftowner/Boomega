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

@file:JvmName("ControlUtils")

package com.dansoftware.boomega.gui.util

import javafx.collections.ObservableList
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView

inline val <T> TableView<T>.selectedItems: ObservableList<T>
    get() = selectionModel.selectedItems

fun <T> ComboBox<T>.refresh() {
    val items: ObservableList<T> = this.items
    val selected: T = this.selectionModel.selectedItem
    this.items = null
    this.items = items
    this.selectionModel.select(selected)
}

/**
 * Determines that a ButtonType's button data is the same.
 */
fun ButtonType.typeEquals(other: ButtonType) = this.buttonData == other.buttonData