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

package com.dansoftware.boomega.gui.control

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ToolBar
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority

open class TwoSideToolBar : HBox() {

    private val leftToolBar: ToolBar = buildLeftToolBar()
    private val rightToolBar: ToolBar = buildRightToolBar()

    val leftItems: ObservableList<Node>
        get() = leftToolBar.items

    val rightItems: ObservableList<Node>
        get() = rightToolBar.items

    init {
        children.add(leftToolBar)
        children.add(SeparatorPane())
        children.add(rightToolBar)
    }

    private fun buildLeftToolBar() = ToolBar().apply {
        setHgrow(this, Priority.SOMETIMES)
        prefHeightProperty().bind(this@TwoSideToolBar.heightProperty())
    }

    private fun buildRightToolBar() = ToolBar().apply {
        setHgrow(this, Priority.SOMETIMES)
        prefHeightProperty().bind(this@TwoSideToolBar.heightProperty())
    }

    private class SeparatorPane : Pane() {
        init {
            styleClass.add("tool-bar")
            setHgrow(this, Priority.ALWAYS)
        }
    }
}