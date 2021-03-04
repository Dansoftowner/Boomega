package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.appdata.Preferences
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass

abstract class PreferencesPane(val preferences: Preferences) : VBox() {

    init {
        styleClass.add(JMetroStyleClass.BACKGROUND)
        padding = Insets(10.0)
    }

    abstract val title: String
    abstract val graphic: Node

    protected fun addEntry(title: String, description: String?, region: Region) {
        children.add(buildEntry(title, description, region))
    }

    private fun buildEntry(title: String, description: String?, region: Region) = HBox(5.0).apply {
        padding = Insets(10.0)
        children.add(buildDescriptionPane(title, description))
        children.add(region.also {
            HBox.setHgrow(it, Priority.ALWAYS)
            region.maxWidth = Double.MAX_VALUE
        })
    }

    private fun buildDescriptionPane(title: String, description: String?) = StackPane().apply {
        HBox.setHgrow(this, Priority.ALWAYS)
        VBox(2.0).apply {
            children.add(Label(title).apply { styleClass.add("entry-title") })
            description?.let { children.add(Label(it).apply { styleClass.add("entry-description") }) }
        }.let { Group(it) }.also { StackPane.setAlignment(it, Pos.CENTER_LEFT) }.let(children::add)
    }
}