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

@file:JvmName("ImageUtils")

package com.dansoftware.boomega.gui.util

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.image.Image
import java.util.function.Consumer

fun loadImage(resource: String, onImageReady: Consumer<Image>) {
    val image = Image(resource, true)
    image.progressProperty().addListener(object : ChangeListener<Number> {
        override fun changed(observable: ObservableValue<out Number>, oldValue: Number, newValue: Number) {
            if (newValue == 1.0 && image.isError.not()) {
                onImageReady.accept(image)
                observable.removeListener(this)
            }
        }
    })
}