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

@file:JvmName("EventUtils")

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.util.invoke

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.stage.Window
import java.util.function.Consumer

fun Node.onWindowPresent(action: Consumer<Window>) {
    onWindowPresent { action(it) }
}

inline fun Node.onWindowPresent(crossinline action: (Window) -> Unit) {
    this.scene?.window?.also { action(it) }
        ?: onScenePresent {
            it.windowProperty().addListener(object : ChangeListener<Window> {
                override fun changed(
                    observable: ObservableValue<out Window>,
                    oldValue: Window?,
                    newValue: Window?
                ) {
                    newValue?.let {
                        action(newValue)
                        observable.removeListener(this)
                    }
                }
            })
        }
}

inline fun Node.onScenePresent(crossinline action: (Scene) -> Unit) {
    this.scene?.also { action(it) }
        ?: sceneProperty().addListener(object : ChangeListener<Scene> {
            override fun changed(observable: ObservableValue<out Scene>, oldValue: Scene?, newValue: Scene?) {
                newValue?.let {
                    action(it)
                    observable.removeListener(this)
                }
            }
        })
}

inline fun Control.onSkinPresent(crossinline action: (Skin<*>) -> Unit) {
    skinProperty().addListener(object : ChangeListener<Skin<*>> {
        override fun changed(observable: ObservableValue<out Skin<*>>, oldValue: Skin<*>?, newValue: Skin<*>?) {
            newValue?.let {
                action(it)
                observable.removeListener(this)
            }
        }
    })
}