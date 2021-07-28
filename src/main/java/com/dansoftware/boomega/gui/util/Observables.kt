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

@file:JvmName("ObservableUtils")

package com.dansoftware.boomega.gui.util

import javafx.beans.binding.Bindings
import javafx.beans.binding.BooleanBinding
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase
import javafx.collections.ObservableList

fun <T> constantObservable(value: () -> T): ObservableValue<T> =
    object : ObservableValueBase<T>() {
        override fun getValue(): T = value()
    }

fun ObservableList<*>.emptyBinding(): BooleanBinding {
    return Bindings.isEmpty(this)
}