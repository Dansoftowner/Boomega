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

package com.dansoftware.boomega.export.excel.gui

import com.dansoftware.boomega.export.api.RecordExportAPI
import com.dansoftware.boomega.export.excel.ExcelExportConfiguration
import com.dansoftware.boomega.export.gui.BaseConfigurationView
import com.dansoftware.boomega.gui.control.FontNamePicker
import com.dansoftware.boomega.gui.util.*
import com.dansoftware.boomega.i18n.api.i18n
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color

@OptIn(RecordExportAPI::class)
class ExcelConfigurationView(
    private val onFinished: (ExcelExportConfiguration) -> Unit
) : BorderPane() {

    private val excelExportConfiguration = ExcelExportConfiguration()

    init {
        VBox.setVgrow(this, Priority.ALWAYS)
        styleClass.addAll("background", "export-configuration-view", "excel-configuration-view")
        buildUI()
    }

    private fun buildUI() {
        center = TabView(excelExportConfiguration)
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
                onFinished(excelExportConfiguration)
            }
        }
    }

    private class TabView(private val excelExportConfiguration: ExcelExportConfiguration) : TabPane() {

        init {
            styleClass.add("underlined")
            buildUI()
        }

        private fun buildUI() {
            tabs.add(tab(i18n("record.export.excel.tab.general"), GeneralView(excelExportConfiguration)))
            tabs.add(tab(i18n("record.export.excel.tab.format"), scrollPane(StyleView(excelExportConfiguration), fitToWidth = true)))
        }

        private fun tab(title: String, content: Node) = Tab(title, content).apply {
            isClosable = false
        }
    }

    private class GeneralView(
        config: ExcelExportConfiguration
    ) : BaseConfigurationView<ExcelExportConfiguration>(config) {
        init {
            styleClass.add("general-view")
        }
    }

    private class StyleView(private val config: ExcelExportConfiguration) : GridPane() {
        init {
            styleClass.add("style-view")
            padding = Insets(20.0)
            hgap = 10.0
            vgap = 10.0
            buildUI()
        }

        private fun buildUI() {
            addTextField(i18n("record.export.excel.sheet_name"), config.sheetName, config::sheetName::set)
            addTextField(i18n("record.export.excel.place_holder_text"), config.emptyCellPlaceHolder, config::emptyCellPlaceHolder::set)
            addSection(i18n("record.export.excel.header"), config.headerCellStyle)
            addSection(i18n("record.export.excel.regular"), config.regularCellStyle)
        }

        private inline fun addTextField(label: String, defaultText: String?, crossinline onTextChanged: (String) -> Unit) {
            addRow(Label(label))
            addRow(TextField(defaultText).apply { textProperty().onValuePresent(onTextChanged) })
        }

        private fun addSection(title: String, cellStyle: ExcelExportConfiguration.CellStyle) {
            addRow(Label(title).styleClass("category-label").colspan(2).hgrow(Priority.ALWAYS))
            addRow(Separator().colspan(2).hgrow(Priority.ALWAYS))
            addRow(Label(i18n("record.export.excel.background_color")))
            addRow(BackgroundColorPicker(cellStyle))
            addRow(Label(i18n("record.export.excel.font")))
            addRow(CellFontChooser(cellStyle))
            addRow(Label(i18n("record.export.excel.font_color")))
            addRow(FontColorPicker(cellStyle))
        }
    }

    private class CellFontChooser(private val cellStyle: ExcelExportConfiguration.CellStyle) : HBox(5.0) {
        init {
            GridPane.setHgrow(this, Priority.ALWAYS)
            buildUI()
        }

        private fun buildUI() {
            children.add(buildFontPicker())
            children.add(buildBoldToggle())
            children.add(buildItalicToggle())
            children.add(buildStrikeThroughToggle())
        }

        private fun buildFontPicker() = FontNamePicker().apply {
            setHgrow(this, Priority.ALWAYS)
            maxWidth = Double.MAX_VALUE
            valueProperty().onValuePresent {
                cellStyle.fontName = it
            }
        }

        private fun buildBoldToggle() = ToggleButton().apply {
            graphic = icon("bold-icon")
            isSelected = cellStyle.isBold
            selectedProperty().onValuePresent { cellStyle.isBold = it }
        }

        private fun buildItalicToggle() = ToggleButton().apply {
            graphic = icon("italic-icon")
            isSelected = cellStyle.isItalic
            selectedProperty().onValuePresent { cellStyle.isItalic = it }
        }

        private fun buildStrikeThroughToggle() = ToggleButton().apply {
            graphic = icon("strikethrough-icon")
            isSelected = cellStyle.isStrikeout
            selectedProperty().onValuePresent { cellStyle.isStrikeout = it }
        }
    }

    private class FontColorPicker(cellStyle: ExcelExportConfiguration.CellStyle) :
        ColorPicker(cellStyle.fontColor?.toFXColor() ?: Color.BLACK) {
        init {
            GridPane.setHgrow(this, Priority.ALWAYS)
            GridPane.setColumnSpan(this, 2)
            maxWidth = Double.MAX_VALUE
            valueProperty().onValuePresent { cellStyle.fontColor = it.toAWTColor() }
        }
    }

    private class BackgroundColorPicker(cellStyle: ExcelExportConfiguration.CellStyle) :
        ColorPicker(cellStyle.backgroundColor?.toFXColor() ?: Color.TRANSPARENT) {
        init {
            GridPane.setHgrow(this, Priority.ALWAYS)
            GridPane.setColumnSpan(this, 2)
            maxWidth = Double.MAX_VALUE
            valueProperty().onValuePresent { cellStyle.backgroundColor = it.toAWTColor() }
        }
    }
}