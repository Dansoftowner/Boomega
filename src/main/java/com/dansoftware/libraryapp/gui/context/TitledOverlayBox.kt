package com.dansoftware.libraryapp.gui.context

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.*

open class TitledOverlayBox(
    title: String,
    graphic: Node,
    content: Node,
    resizableH: Boolean,
    resizableV: Boolean
) : StackPane(Group(ResizablePane(InnerVBox(title, graphic, content, resizableH, resizableV)))) {

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
        val resizableV: Boolean
    ) : BorderPane() {

        companion object {
            const val RESIZE_UNIT = 35.0
        }

        init {
            styleClass.add("title-bar")
            left = graphic.also { setAlignment(it, Pos.CENTER) }
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
                if (resizableV) {
                    hBox.children.add(buildButton(MaterialDesignIcon.ARROW_UP) {
                        content.prefHeight = content.height + RESIZE_UNIT
                    })
                    hBox.children.add(buildButton(MaterialDesignIcon.ARROW_DOWN) {
                        content.prefHeight = content.height - RESIZE_UNIT
                    })
                }
                if (resizableH) {
                    hBox.children.add(buildButton(MaterialDesignIcon.ARROW_LEFT) {
                        content.prefWidth = content.width - RESIZE_UNIT
                    })
                    hBox.children.add(buildButton(MaterialDesignIcon.ARROW_RIGHT) {
                        content.prefWidth = content.width + RESIZE_UNIT
                    })
                }
            }

        private fun buildButton(icon: MaterialDesignIcon, action: EventHandler<ActionEvent>): Node =
            Button(null, MaterialDesignIconView(icon)).also {
                it.onAction = action
                it.padding = Insets(0.0)
            }

    }

    private class InnerVBox(
        title: String,
        graphic: Node,
        content: Node,
        val isResizableH: Boolean,
        val isResizableV: Boolean
    ) : VBox() {
        init {
            setVgrow(this, Priority.ALWAYS)
            styleClass.add("overlay-box")
            children.add(TitleBar(title, graphic, this, isResizableH, isResizableV))
            children.add(content)
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