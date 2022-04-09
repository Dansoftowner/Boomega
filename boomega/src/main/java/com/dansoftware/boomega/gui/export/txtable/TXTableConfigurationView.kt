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

package com.dansoftware.boomega.gui.export.txtable

import com.dansoftware.boomega.export.txtable.TXTableConfiguration
import com.dansoftware.boomega.gui.export.control.BaseConfigurationView
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.api.i18n
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*
import jfxtras.styles.jmetro.JMetroStyleClass
import jfxtras.styles.jmetro.JMetroStyleClass.BACKGROUND

class TXTableConfigurationView(
    private val onFinished: (TXTableConfiguration) -> Unit
) : BorderPane() {

    private val exportConfiguration = TXTableConfiguration()

    init {
        styleClass.addAll(BACKGROUND, "export-configuration-view", "txtable-configuration-view")
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
            tabs.add(tab("formatting", scrollPane(StyleView(configuration), fitToWidth = true)))
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
            addGeneralSection()
            addHeaderSection()
            addRegularSection()
        }

        private fun addGeneralSection() {
            addOneCharTextField("intersect:", config.border.intersect.toString(), config.border::intersect::set)
            addOneCharTextField(
                "horizontal separator:",
                config.border.horizontal.toString(),
                config.border::horizontal::set
            )
            addOneCharTextField("vertical separator:", config.border.vertical.toString(), config.border::vertical::set)
        }

        private fun addHeaderSection() {
            addSectionLabel("Header row")
            addOneCharTextField(
                "Place holder:",
                config.headerPlaceHolderChar.toString(),
                config::headerPlaceHolderChar::set
            )
            addIntegerField(
                "Min height",
                defaultValue = config.headerHeight,
                onValueChanged = config::headerHeight::set
            )
            addIntegerField(
                "Min width",
                defaultValue = config.headerMinWidth,
                onValueChanged = config::headerMinWidth::set
            )
        }


        private fun addRegularSection() {
            addSectionLabel("Regular rows")
            addOneCharTextField(
                "Place holder:",
                config.regularPlaceHolderChar.toString(),
                config::regularPlaceHolderChar::set
            )
            addTextField(
                "Null value place holder:",
                config.nullValuePlaceHolder,
                config::nullValuePlaceHolder::set
            )
            addIntegerField(
                "Min height",
                defaultValue = config.regularHeight,
                onValueChanged = config::regularHeight::set
            )
            addIntegerField(
                "Min width",
                defaultValue = config.regularMinWidth,
                onValueChanged = config::regularMinWidth::set
            )
        }

        private fun addSectionLabel(title: String) {
            addRow(Label(title).styleClass("category-label").colspan(2).hgrow(Priority.ALWAYS))
            addRow(Separator().colspan(2).hgrow(Priority.ALWAYS))
        }

        private inline fun addOneCharTextField(
            label: String,
            defaultText: String?,
            crossinline onTextChanged: (Char) -> Unit
        ) {
            addRow(Label(label))
            addRow(buildOneCharTextField(defaultText) {
                onTextChanged(it.getOrElse(0) { ' ' })
            })
        }

        private inline fun addIntegerField(
            label: String,
            min: Int = 1,
            max: Int = Integer.MAX_VALUE,
            defaultValue: Int = min,
            crossinline onValueChanged: (Int) -> Unit
        ) {
            addRow(Label(label))
            addRow(buildIntegerField(min, max, defaultValue, onValueChanged))
        }

        private inline fun addTextField(
            label: String,
            defaultText: String?,
            crossinline onTextChanged: (String) -> Unit
        ) {
            addRow(Label(label))
            addRow(buildTextField(defaultText, onTextChanged))
        }

        private inline fun buildIntegerField(
            min: Int = 1,
            max: Int = Integer.MAX_VALUE,
            defaultValue: Int = min,
            crossinline onValueChanged: (Int) -> Unit
        ) = Spinner<Int>(min, max, defaultValue).apply {
            valueProperty().addListener { _, _, newValue ->
                onValueChanged(newValue)
            }
        }

        private inline fun buildOneCharTextField(defaultText: String?, crossinline onTextChanged: (String) -> Unit) =
            TextField(defaultText).apply {
                setHgrow(this, Priority.ALWAYS)
                maxWidth = Double.MAX_VALUE
                textProperty().addListener { _, oldValue, newValue ->
                    if (newValue.length > 1)
                        textProperty().set(oldValue)
                    onTextChanged(newValue)
                }
            }

        private inline fun buildTextField(
            defaultText: String?,
            crossinline onTextChanged: (String) -> Unit
        ) = TextField(defaultText).apply {
            setHgrow(this, Priority.ALWAYS)
            maxWidth = Double.MAX_VALUE
            textProperty().addListener { _, _, newValue ->
                onTextChanged(newValue)
            }
        }

        // TODO: finish h-algin control
        // TODO: v-align control

        private class HAlignControl(onValueChanged: (TXTableConfiguration.HorizontalAlignment) -> Unit) : HBox(3.0) {

            private val radioGroup = ToggleGroup().apply {
                selectedToggleProperty().addListener { _, _, toggle ->

                }
            }

            init {
            }

            private fun buildButton(
                icon: Node,
                tooltipText: String,
                align: TXTableConfiguration.HorizontalAlignment
            ) = Button().apply {
                contentDisplay = ContentDisplay.GRAPHIC_ONLY
                tooltip = Tooltip(tooltipText)
                graphic = icon
                properties["align-value"] = align
            }
        }
    }
}