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

package com.dansoftware.boomega.gui.export.txtable

import com.dansoftware.boomega.export.txtable.TXTableConfiguration
import com.dansoftware.boomega.gui.export.control.BaseConfigurationView
import com.dansoftware.boomega.gui.util.addRow
import com.dansoftware.boomega.gui.util.scrollPane
import com.dansoftware.boomega.i18n.i18n
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.JMetroStyleClass.BACKGROUND

class TXTableConfigurationView(
    private val onFinished: (TXTableConfiguration) -> Unit
) : BorderPane() {

    private val exportConfiguration = TXTableConfiguration()

    init {
        styleClass.addAll(BACKGROUND, "txtable-configuration-view")
        buildUI()
    }

    private fun buildUI() {
        center = TabView(exportConfiguration)
        bottom = ExecuteArea()
    }

    private inner class ExecuteArea : StackPane() {
        init {
            padding = Insets(0.0, 20.0, 20.0, 20.0)
            children.add(buildExecuteButton())
        }

        private fun buildExecuteButton() = Button().apply {
            maxWidth = Double.MAX_VALUE
            isDefaultButton = true
            text = i18n("record.export.execute")
            setOnAction {
                onFinished(exportConfiguration)
            }
        }
    }

    private class TabView(private val configuration: TXTableConfiguration) : TabPane() {

        init {
            styleClass.add(JMetroStyleClass.UNDERLINE_TAB_PANE)
            buildUI()
        }

        private fun buildUI() {
            // TODO: i18n
            tabs.add(tab("general", GeneralView(configuration)))
            tabs.add(tab("formatting", scrollPane(StyleView(configuration))))
        }

        private fun tab(title: String, content: Node) = Tab(title, content).apply {
            isClosable = false
        }
    }

    private class GeneralView(exportConfiguration: TXTableConfiguration) :
        BaseConfigurationView<TXTableConfiguration>(exportConfiguration) {
            init {
                styleClass.add("general-view")
            }
        }

    private class StyleView(private val config: TXTableConfiguration) : GridPane() {
        init {
            styleClass.add("style-view")
            padding = Insets(20.0)
            hgap = 10.0
            vgap = 10.0
            buildUI()
        }

        private fun buildUI() {
            // TODO: i18n

            addTextField("intersect:", config.border.intersect.toString()) {
                config.border.intersect = it.getOrElse(0) { ' ' }
            }
            addTextField("horizontal separator:", config.border.horizontal.toString()) {
                config.border.horizontal = it.getOrElse(0) { ' ' }
            }
            addTextField("vertical separator:", config.border.vertical.toString()) {
                config.border.vertical = it.getOrElse(0) { ' ' }
            }
        }


        private inline fun addTextField(label: String, defaultText: String?, crossinline onTextChanged: (String) -> Unit) {
            addRow(Label(label))
            addRow(TextField(defaultText).apply {
                textProperty().addListener { _, oldValue, newValue ->
                    if (newValue.length > 1)
                        textProperty().set(oldValue)
                }

            })
        }

    }
}