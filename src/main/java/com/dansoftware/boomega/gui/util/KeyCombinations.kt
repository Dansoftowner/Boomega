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

@file:JvmName("KeyUtils")
@file:Suppress("NOTHING_TO_INLINE")

package com.dansoftware.boomega.gui.util

import com.dansoftware.boomega.gui.keybinding.KeyBinding
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Region
import java.lang.ref.WeakReference

/**
 * Utility function that converts a [KeyCodeCombination] into a [KeyEvent] object,
 * simulating that the particular key-combination is pressed by the user
 */
inline fun KeyCodeCombination.asKeyEvent(): KeyEvent =
    KeyEvent(
        KeyEvent.KEY_PRESSED,
        this.code.toString(),
        this.displayText,
        this.code,
        this.shift == KeyCombination.ModifierValue.DOWN,
        this.control == KeyCombination.ModifierValue.DOWN,
        this.alt == KeyCombination.ModifierValue.DOWN,
        this.meta == KeyCombination.ModifierValue.DOWN
    )

inline fun KeyEvent.asKeyCombination(): KeyCombination? =
    mutableListOf<KeyCombination.Modifier>().also { modifiers ->
        this.isControlDown.takeIf { it }?.let { modifiers.add(KeyCombination.CONTROL_DOWN) }
        this.isAltDown.takeIf { it }?.let { modifiers.add(KeyCombination.ALT_DOWN) }
        this.isShiftDown.takeIf { it }?.let { modifiers.add(KeyCombination.SHIFT_DOWN) }
        this.isMetaDown.takeIf { it }?.let { modifiers.add(KeyCombination.META_DOWN) }
        this.isShortcutDown.takeIf { it }?.let { modifiers.add(KeyCombination.SHORTCUT_DOWN) }
    }.let {
        this.code?.let { _ ->
            try {
                when {
                    it.isEmpty()
                        .and(this.code.isFunctionKey.not())
                        .and(this.code.isNavigationKey.not())
                        .and(this.code != KeyCode.DELETE)
                        .and(this.code != KeyCode.INSERT) -> throw RuntimeException()
                    else -> KeyCodeCombination(this.code, *it.toTypedArray())
                }
            } catch (e: RuntimeException) {
                null
            }
        }
    }

inline fun KeyEvent.isOnlyCode(): Boolean {
    return listOf(
        this.isControlDown.takeIf { it },
        this.isAltDown.takeIf { it },
        this.isShiftDown.takeIf { it },
        this.isMetaDown.takeIf { it },
        this.isShortcutDown.takeIf { it }
    ).count { it !== null } == 0
}

inline fun KeyEvent.isUndefined(): Boolean =
    this.code.name.equals("undefined", ignoreCase = true)


fun Region.addKeyBindingDetection(keyBinding: KeyBinding, action: (KeyBinding) -> Unit) {
    onScenePresent {
        it.addKeyBindingDetection(this, keyBinding, action)
    }
}

private fun Scene.addKeyBindingDetection(node: Node, keyBinding: KeyBinding, action: (KeyBinding) -> Unit) {
    // binding the action to the node ( for avoiding losing it before the node is removed from memory )
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