package com.dansoftware.boomega.gui.control

import javafx.collections.ObservableList
import javafx.geometry.NodeOrientation
import javafx.scene.Node
import javafx.scene.control.ToolBar
import javafx.scene.layout.HBox
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
        children.add(rightToolBar)
    }

    private fun buildLeftToolBar() = ToolBar().apply {
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private fun buildRightToolBar() = ToolBar().apply {
        HBox.setHgrow(this, Priority.ALWAYS)
        nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
    }
}