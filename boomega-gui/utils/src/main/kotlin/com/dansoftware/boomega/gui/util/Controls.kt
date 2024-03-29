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

@file:JvmName("ControlUtils")
@file:Suppress("NOTHING_TO_INLINE")

package com.dansoftware.boomega.gui.util

import javafx.beans.value.ObservableBooleanValue
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment
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

inline var <T> ComboBox<T>.selectedItem: T
    get() = selectionModel.selectedItem
    set(value) {
        selectionModel.select(value)
    }

inline fun <T> ChoiceBox<T>.selectedItemProperty() = selectionModel.selectedItemProperty()

inline fun <T> ComboBox<T>.selectedItemProperty() = selectionModel.selectedItemProperty()

inline fun <T> ComboBox<T>.refresh() {
    val items: ObservableList<T> = this.items
    val selected: T = this.selectionModel.selectedItem
    this.items = null
    this.items = items
    this.selectionModel.select(selected)
}

inline fun <T> ChoiceBox<T>.valueConvertingPolicy(
    crossinline toStringFun: (T) -> String?,
    crossinline fromStringFun: (String?) -> T
) {
    converter = object : StringConverter<T>() {
        override fun toString(obj: T) = toStringFun(obj)
        override fun fromString(string: String) = fromStringFun(string)
    }
}

inline fun <T> ComboBox<T>.valueConvertingPolicy(
    crossinline toStringFun: (T) -> String?,
    crossinline fromStringFun: (String?) -> T
) {
    converter = object : StringConverter<T>() {
        override fun toString(obj: T) = toStringFun(obj)
        override fun fromString(string: String) = fromStringFun(string)
    }
}

/**
 * Determines that a ButtonType's button data is the same.
 */
inline fun ButtonType.typeEquals(other: ButtonType) = (buttonData == other.buttonData)

@Suppress("UNCHECKED_CAST", "EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Node> Node.lookup(selector: String): T? = lookup(selector) as? T

/**
 * Wraps the given [Node] into a [StackPane]
 */
inline fun Node.asCentered(pos: Pos = Pos.CENTER) = StackPane().also {
    val content = if (this is Parent) Group(this) else this
    StackPane.setAlignment(content, pos)
    it.children.add(content)
}

inline fun hyperLink(text: String, graphic: Node? = null, crossinline onAction: () -> Unit) =
    Hyperlink(text, graphic).apply {
        setOnAction { onAction() }
    }

inline fun scrollPane(content: Node, fitToWidth: Boolean = false, fitToHeight: Boolean = false) = ScrollPane(content).apply {
    isFitToWidth = fitToWidth
    isFitToHeight = fitToHeight
}

inline fun GridPane.addRow(vararg elements: Node) = apply {
    addRow(rowCount, *elements)
}

inline fun <T : Node> T.styleClass(styleClass: String) = apply {
    getStyleClass().add(styleClass)
}

inline fun <T : Node> T.colspan(value: Int) = apply {
    GridPane.setColumnSpan(this, value)
}

inline fun <T : Node> T.hgrow(priority: Priority) = apply {
    HBox.setHgrow(this, priority)
    GridPane.setHgrow(this, priority)
}

inline fun <T : Node> T.vgrow(priority: Priority) = apply {
    VBox.setVgrow(this, priority)
    GridPane.setVgrow(this, priority)
}

inline fun <T : Node> T.stackPaneAlignment(pos: Pos) = apply {
    StackPane.setAlignment(this, pos)
}

inline fun <T : Control> T.tooltip(value: String) = apply {
    tooltip = Tooltip(value)
}

inline fun <T : Region> T.padding(value: Insets) = apply {
    padding = value
}

inline fun <T : Label> T.textAlignment(value: TextAlignment) = apply {
    textAlignment = value
}

/**
 * Binds the node's [Node.visibleProperty] and [Node.managedProperty] to the given observable value
 */
inline infix fun <T : Node> T.bindFullVisibilityTo(to: ObservableBooleanValue) {
    visibleProperty().bind(to)
    managedProperty().bind(to)
}

/**
 * A [TextField] implementation accepts only numbers
 */
class NumberTextField : TextField() {
    init {
        textProperty().addListener { _, oldValue, newValue ->
            if (newValue.isNotEmpty() && newValue.matches("\\d+".toRegex()).not())
                textProperty().set(oldValue)
        }
    }
}