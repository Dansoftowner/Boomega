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

package com.dansoftware.boomega.gui.base

import com.dansoftware.boomega.gui.util.icon
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.*

open class TitledOverlayBox(
    title: String,
    graphic: Node,
    content: Node,
    resizableH: Boolean,
    resizableV: Boolean,
    vararg customTitleBarItems: Node
) : StackPane(Group(ResizablePane(InnerVBox(title, graphic, content, resizableH, resizableV, customTitleBarItems)))) {

    constructor(title: String, graphic: Node, content: Node) :
            this(title, graphic, content, true, true)

    init {
        this.isPickOnBounds = false
    }

    private class TitleBar(
        title: String,
        graphic: Node,
        content: VBox,
        val resizableH: Boolean,
        val resizableV: Boolean,
        val customTitleBarItems: Array<out Node>
    ) : BorderPane() {

        companion object {
            const val RESIZE_UNIT = 35.0
        }

        init {
            styleClass.add("title-bar")
            left = graphic.also {
                setAlignment(it, Pos.CENTER)
                setMargin(it, Insets(0.0, 0.0, 0.0, 5.0))
            }
            center = buildTitleLabel(title)
            right = buildRightBox(content)
        }

        private fun buildTitleLabel(title: String) = Label(title).also {
            setAlignment(it, Pos.CENTER_LEFT)
            setMargin(it, Insets(0.0, 0.0, 0.0, 5.0))
        }

        private fun buildRightBox(content: VBox): Node =
            HBox(5.0).also { hBox ->
                setAlignment(hBox, Pos.CENTER_RIGHT)
                when {
                    customTitleBarItems.isNullOrEmpty().not() -> {
                        customTitleBarItems.forEach { hBox.children.add(it) }
                        hBox.children.add(Separator(Orientation.VERTICAL))
                    }
                }
                if (resizableV) {
                    hBox.children.add(buildButton("arrow-up-icon") {
                        content.prefHeight = content.height + RESIZE_UNIT
                    })
                    hBox.children.add(buildButton("arrow-down-icon") {
                        content.prefHeight = content.height - RESIZE_UNIT
                    })
                }
                if (resizableH) {
                    hBox.children.add(buildButton("arrow-left-icon") {
                        content.prefWidth = content.width - RESIZE_UNIT
                    })
                    hBox.children.add(buildButton("arrow-right-icon") {
                        content.prefWidth = content.width + RESIZE_UNIT
                    })
                }
            }

        private fun buildButton(iconStyleClass: String, action: EventHandler<ActionEvent>): Node =
            Button(null, icon(iconStyleClass)).also {
                it.onAction = action
                it.padding = Insets(0.0)
            }

    }

    private class InnerVBox(
        title: String,
        graphic: Node,
        content: Node,
        val isResizableH: Boolean,
        val isResizableV: Boolean,
        customTitleBarItems: Array<out Node>
    ) : VBox() {
        init {
            setVgrow(this, Priority.ALWAYS)
            styleClass.add("overlay-box")
            children.add(TitleBar(title, graphic, this, isResizableH, isResizableV, customTitleBarItems))
            children.add(content.also { it.styleClass.add("content") })
        }
    }

    private class ResizablePane(val content: InnerVBox) : BorderPane(content) {

        companion object {
            const val PREFERRED_RESIZE_AREA_SIZE = 3.5
            const val PREFERRED_RESIZE_SPEED = 2.0
        }

        init {
            if (content.isResizableV) {
                top = buildTop()
                bottom = buildBottom()
            }
            if (content.isResizableH) {
                right = buildRight()
                left = buildLeft()
            }
        }

        private fun buildTop() = StackPane().also {
            it.cursor = Cursor.V_RESIZE
            it.prefHeight = PREFERRED_RESIZE_AREA_SIZE
            var released = false
            var lastY: Double? = null
            it.setOnMouseReleased { released = true }
            it.setOnMouseDragged { event ->
                if (lastY === null || released) {
                    lastY = event.sceneY
                    released = false
                }
                content.prefHeight = content.height - (event.sceneY - lastY!!) * PREFERRED_RESIZE_SPEED
                lastY = event.sceneY
            }
        }

        private fun buildBottom() = StackPane().also {
            it.cursor = Cursor.V_RESIZE
            it.prefHeight = PREFERRED_RESIZE_AREA_SIZE
            var released = false
            var lastY: Double? = null
            it.setOnMouseReleased { released = true }
            it.setOnMouseDragged { event ->
                if (lastY === null || released) {
                    lastY = event.sceneY
                    released = false
                }
                content.prefHeight = content.height + (event.sceneY - lastY!!) * PREFERRED_RESIZE_SPEED
                lastY = event.sceneY
            }
        }

        private fun buildLeft() = StackPane().also {
            it.cursor = Cursor.H_RESIZE
            it.prefWidth = PREFERRED_RESIZE_AREA_SIZE
            var released = false
            var lastX: Double? = null
            it.setOnMouseReleased { released = true }
            it.setOnMouseDragged { event ->
                if (lastX === null || released) {
                    lastX = event.sceneX
                    released = false
                }
                content.prefWidth = content.width - (event.sceneX - lastX!!) * PREFERRED_RESIZE_SPEED
                lastX = event.sceneX
            }
        }

        private fun buildRight() = StackPane().also {
            it.cursor = Cursor.H_RESIZE
            it.prefWidth = PREFERRED_RESIZE_AREA_SIZE
            var released = false
            var lastX: Double? = null
            it.setOnMouseReleased { released = true }
            it.setOnMouseDragged { event ->
                if (lastX === null || released) {
                    lastX = event.sceneX
                    released = false
                }
                content.prefWidth = content.width + (event.sceneX - lastX!!) * PREFERRED_RESIZE_SPEED
                lastX = event.sceneX
            }
        }
    }
}