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

@file:JvmName("WindowUtils")

package com.dansoftware.boomega.gui.util

import javafx.scene.Node
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import javafx.stage.WindowEvent
import java.util.*

/**
 * Finds the corresponding [Window] to the particular [Node] object.
 *
 * @param node the node to find the window of
 * @return the [Window] object
 */
val Node?.window: Window?
    get() = this?.scene?.window

/**
 * Gets the node's window as a [Stage] instance
 *
 * @see window
 */
val Node?.stage: Stage?
    get() = window as? Stage

@Deprecated("")
fun getWindowOptionalOf(node: Node?): Optional<Window> =
    Optional.ofNullable(node?.scene?.window)

@Deprecated("")
fun getStageOptionalOf(node: Node?): Optional<Stage?> =
    getWindowOptionalOf(node).map { window: Window? -> window as? Stage }

/**
 * Returns all [Stage]s that is owned by this [Stage].
 */
val Stage.ownedStages: List<Stage>
    get() = Window.getWindows().asSequence()
        .filterIsInstance<Stage>()
        .filter { it.owner == this }
        .toList()

/**
 * Wraps a stage into a "shadowed" stage that doesn't appear on the task-bar
 */
fun Stage.shadowed() =
    Stage(StageStyle.UTILITY).apply {
        opacity = 0.0
        this@shadowed.initOwner(this)
        addEventHandler(WindowEvent.WINDOW_SHOWN) { this@shadowed.show() }
        addEventHandler(WindowEvent.WINDOW_HIDING) { this@shadowed.hide() }
    }