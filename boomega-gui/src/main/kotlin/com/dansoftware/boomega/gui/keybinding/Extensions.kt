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

package com.dansoftware.boomega.gui.keybinding

import com.dansoftware.boomega.gui.util.onScenePresent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Region
import java.lang.ref.WeakReference

fun <M : MenuItem> M.keyBinding(keyBinding: KeyBinding?) = apply {
    keyBinding?.let {
        acceleratorProperty().bind(it.keyCombinationProperty)
    }
}

fun Region.addKeyBindingDetection(keyBinding: KeyBinding, action: (KeyBinding) -> Unit) {
    onScenePresent {
        it.addKeyBindingDetection(this, keyBinding, action)
    }
}

private fun Scene.addKeyBindingDetection(node: Node, keyBinding: KeyBinding, action: (KeyBinding) -> Unit) {
    // binding the action to the node ( to avoid losing it before the node is removed from memory )
    node.properties["keyBindDetectionActionCache"]?.run {
        @Suppress("UNCHECKED_CAST")
        takeIf { it is MutableList<*> }?.let { it as MutableList<Any> }?.add(action)
            ?: throw RuntimeException()
    } ?: run {
        node.properties["keyBindDetectionActionCache"] = mutableListOf(action)
    }

    val nodeWeakReference = WeakReference(node)
    val actionWeakReference = WeakReference(action)

    var eventHandler: EventHandler<KeyEvent>? = null
    eventHandler = EventHandler {
        if (nodeWeakReference.get()?.scene == this) {
            if (keyBinding.match(it))
                actionWeakReference.get()?.invoke(keyBinding)
        } else {
            removeEventHandler(KeyEvent.KEY_PRESSED, eventHandler)
        }
    }
    addEventHandler(KeyEvent.KEY_PRESSED, eventHandler)
}