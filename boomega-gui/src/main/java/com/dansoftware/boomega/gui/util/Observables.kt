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

@file:JvmName("ObservableUtils")
@file:Suppress("NOTHING_TO_INLINE")

package com.dansoftware.boomega.gui.util

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.*
import javafx.collections.ObservableList

inline fun <T> constantObservable(crossinline value: () -> T): ObservableValue<T> =
    object : ObservableValueBase<T>() {
        override fun getValue(): T = value()
    }

inline fun ObservableList<*>.emptyBinding(): BooleanBinding {
    return Bindings.isEmpty(this)
}

inline fun <T> ObservableValue<T>.onValuePresent(crossinline action: (newValue: T) -> Unit) {
    addListener { _, _, newValue -> action(newValue) }
}

inline fun ObservableBooleanValue.not(): BooleanBinding =
    Bindings.createBooleanBinding({ this.get().not() }, this)

inline fun ObservableStringValue.asStringProperty(): StringProperty =
    SimpleStringProperty().apply {
        bind(this@asStringProperty)
    }

inline fun <O> ObservableObjectValue<O>.asObjectProperty(): ObjectProperty<O> =
    SimpleObjectProperty<O>().apply {
        bind(this@asObjectProperty)
    }