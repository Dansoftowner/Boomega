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
@file:Suppress("NOTHING_TO_INLINE")

package com.dansoftware.boomega.gui.util

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.*
import javafx.util.StringConverter
import org.controlsfx.control.CheckListView

inline val <T> TableView<T>.selectedItems: ObservableList<T>
    get() = selectionModel.selectedItems

inline val <T> ListView<T>.selectedItems: ObservableList<T>
    get() = selectionModel.selectedItems

inline val <T> CheckListView<T>.checkedItems: ObservableList<T>
    get() = checkModel.checkedItems

inline var <T> ChoiceBox<T>.selectedItem: T
    get() = selectionModel.selectedItem
    set(value) {
        selectionModel.select(value)
    }

inline fun <T> ChoiceBox<T>.selectedItemProperty() =
    selectionModel.selectedItemProperty()

inline fun <T> ChoiceBox<T>.valueConvertingPolicy(
    crossinline toStringFun: (T) -> String,
    crossinline fromStringFun: (String) -> T
) {
    converter = object : StringConverter<T>() {
        override fun toString(obj: T) = toStringFun(obj)
        override fun fromString(string: String) = fromStringFun(string)
    }
}

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

fun hyperLink(text: String, graphic: Node? = null, onAction: () -> Unit) = Hyperlink(text, graphic).apply {
    setOnAction { onAction() }
}

fun Node.styleClass(styleClass: String) = apply {
    getStyleClass().add(styleClass)
}

@Suppress("UNCHECKED_CAST", "EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Node> Node.lookup(selector: String): T? = lookup(selector) as? T