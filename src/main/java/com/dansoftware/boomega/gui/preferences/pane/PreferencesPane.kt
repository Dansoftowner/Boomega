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

package com.dansoftware.boomega.gui.preferences.pane

import com.dansoftware.boomega.config.Preferences
import javafx.beans.property.BooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass
import org.controlsfx.control.ToggleSwitch
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class PreferencesPane(val preferences: Preferences) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PreferencesPane::class.java)
    }

    abstract val title: String
    abstract val graphic: Node

    private var cachedContent: Content? = null

    fun getContent(): Content {
        return cachedContent ?: buildContent().also {
            logger.debug("Building content for ${javaClass.simpleName}...")
            cachedContent = it
        }
    }

    protected abstract fun buildContent(): Content

    open class Content : GridPane() {

        val items: ObservableList<PreferencesControl> =
            FXCollections.observableArrayList<PreferencesControl>().apply {
                addListener(ListChangeListener { change ->
                    while (change.next()) {
                        change.addedSubList.forEach { it.addNode(this@Content) }
                    }
                })
            }

        init {
            styleClass.add(JMetroStyleClass.BACKGROUND)
            padding = Insets(10.0)
            hgap = 100.0
            vgap = 20.0
        }

    }

    abstract class PreferencesControl {
        abstract fun addNode(parent: GridPane)
    }

    open class PairControl<T : Region>(val title: String, val description: String? = null, val customControl: T) :
        PreferencesControl() {
        override fun addNode(parent: GridPane) {
            parent.rowCount.also { row ->
                parent.children.add(buildDescriptionPane(title, description)
                    .also { GridPane.setConstraints(it, 0, row) })

                parent.children.add(buildBaseControl().also {
                    GridPane.setConstraints(it, 1, row)
                    GridPane.setHgrow(it, Priority.ALWAYS)
                    customControl.maxWidth = Double.MAX_VALUE
                })
            }
        }

        protected open fun buildBaseControl(): Region = customControl

        private fun buildDescriptionPane(title: String, description: String?) = VBox(2.0).apply {
            children.add(Label(title).apply { styleClass.add("entry-title") })
            description?.let { children.add(Label(it).apply { styleClass.add("entry-description") }) }
        }
    }

    class ToggleControl(title: String, description: String?) :
        PairControl<ToggleSwitch>(title, description, ToggleSwitch()) {

        var isSelected: Boolean
            get() = customControl.isSelected
            set(value) {
                customControl.isSelected = value
            }

        override fun buildBaseControl(): Region =
            Group(customControl).let {
                StackPane.setAlignment(it, Pos.CENTER_RIGHT)
                StackPane(it)
            }

        fun selectedProperty(): BooleanProperty = customControl.selectedProperty()
    }

    class SimpleControl(val customControl: Region) : PreferencesControl() {
        override fun addNode(parent: GridPane) {
            GridPane.setConstraints(customControl, 0, parent.rowCount, 2, 1)
            parent.children.add(customControl)
        }
    }
}