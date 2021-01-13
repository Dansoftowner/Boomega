package com.dansoftware.libraryapp.gui.context

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

private class TitledOverlayBox(title: String, graphic: Node, content: Node) :
    StackPane(Group(VBox(10.0, TitleBar(title, graphic), content).also { it.styleClass.add("overlay-box") })) {

    init {
        this.isPickOnBounds = false
    }

    private class TitleBar(title: String, graphic: Node) : HBox(5.0) {
        init {
            styleClass.add("title-bar")
            children.add(graphic)
            children.add(Label(title))
        }
    }
}