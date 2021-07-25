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
import javafx.stage.Window
import java.util.*
import java.util.stream.Collectors

/**
 * Finds the corresponding [Window] to the particular [Node] object.
 *
 * @param node the node to find the window of
 * @return the [Window] object
 */
val Node?.window: Window?
    get() = this?.scene?.window

/**
 * @see .getWindowOf
 */
val Node?.stage: Stage?
    get() = window as? Stage

fun getWindowOptionalOf(node: Node?): Optional<Window> =
    Optional.ofNullable(node?.scene?.window)


fun getStageOptionalOf(node: Node?): Optional<Stage?> =
    getWindowOptionalOf(node).map { window: Window? -> window as? Stage }

/**
 * Returns all [Stage]s that is owned by this [Stage].
 */
val Stage.ownedStages: List<Stage>
    get() = Window.getWindows().stream()
        .filter { window: Window? -> window is Stage }
        .map { window: Window -> window as Stage }
        .filter { window: Stage -> window.owner == this }
        .collect(Collectors.toList())
